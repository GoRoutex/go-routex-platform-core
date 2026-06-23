package platform.payment.service.application.services.impl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import platform.core.common.service.common.RequestContext;
import platform.core.common.service.domain.booking.PaymentStatus;
import platform.core.common.service.domain.payment.model.PaymentAggregate;
import platform.payment.service.application.command.payment.GetPaymentUrlCommand;
import platform.payment.service.application.services.VNPayService;
import platform.payment.service.domain.payment.port.PaymentEventPublisherPort;
import platform.core.common.service.domain.payment.port.PaymentRepositoryPort;
import platform.payment.service.infrastructure.integration.config.VNPayProperties;
import platform.payment.service.infrastructure.integration.utils.VNPayUtils;
import platform.payment.service.interfaces.model.vnpay.VNPayIpnResponse;
import vn.com.go.routex.identity.security.log.SystemLog;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;


@RequiredArgsConstructor
@Service
public class VNPayServiceImpl implements VNPayService {

    private final VNPayUtils vnPayUtils;
    private final VNPayProperties vnPayProperties;
    private final PaymentRepositoryPort paymentRepositoryPort;
    private final PaymentEventPublisherPort paymentEventPublisherPort;
    private final SystemLog sLog = SystemLog.getLogger(this.getClass());

    @Override
    public String createPaymentUrl(GetPaymentUrlCommand request, String txnRef) {
        String vnp_Version = "2.1.0";
        String vnp_Command = "pay";
        String orderType = "other";
        String amount = request.amount()
                .multiply(BigDecimal.valueOf(100))
                .setScale(0, RoundingMode.HALF_UP)
                .toPlainString();

        String bankCode = request.bankCode();
        String vnp_IpAddr = hasText(request.clientIp()) ? request.clientIp().trim() : "127.0.0.1";

        String vnp_TmnCode = vnPayProperties.tmnCode();

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", amount);
        vnp_Params.put("vnp_CurrCode", "VND");

        if (bankCode != null && !bankCode.isEmpty()) {
            vnp_Params.put("vnp_BankCode", bankCode);
        }

        vnp_Params.put("vnp_TxnRef", txnRef);
        vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang:" + request.bookingCode());
        vnp_Params.put("vnp_OrderType", orderType);
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", vnPayProperties.returnUrl());
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        String hashData = vnPayUtils.buildHashData(vnp_Params);
        String queryUrl = hashData;
        String vnp_SecureHash = vnPayUtils.hmacSHA512(vnPayProperties.hashSecret(), hashData);
        sLog.info("[VNPAY-CREATE] tmnCode={} amount={} hashData={} secureHash={}",
                vnp_TmnCode, amount, hashData, vnp_SecureHash);
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        return vnPayProperties.payUrl() + "?" + queryUrl;
    }

    @Override
    public VNPayIpnResponse processIpn(HttpServletRequest servletRequest) {
        try {
            Map<String, String> fields = collectEncodedFields(servletRequest);
            String vnpSecureHash = servletRequest.getParameter("vnp_SecureHash");

            fields.remove("vnp_SecureHashType");
            fields.remove("vnp_SecureHash");

            String decodedHashData = vnPayUtils.buildHashData(fields);
            String decodedSignValue = vnPayUtils.hmacSHA512(vnPayProperties.hashSecret(), decodedHashData);
            String rawHashData = buildRawHashData(servletRequest.getQueryString());
            String rawSignValue = hasText(rawHashData)
                    ? vnPayUtils.hmacSHA512(vnPayProperties.hashSecret(), rawHashData)
                    : null;

            if (!matchesSecureHash(decodedSignValue, vnpSecureHash)
                    && !matchesSecureHash(rawSignValue, vnpSecureHash)) {
                sLog.warn("[VNPAY-IPN] Invalid checksum decodedHashData={} decodedSignValue={} rawHashData={} rawSignValue={} vnpSecureHash={} fields={}",
                        decodedHashData, decodedSignValue, rawHashData, rawSignValue, vnpSecureHash, fields);
                return ipnResponse("97", "Invalid Checksum");
            }

            String txnRef = servletRequest.getParameter("vnp_TxnRef");
            if (!hasText(txnRef)) {
                return ipnResponse("01", "Order not Found");
            }

            PaymentAggregate payment = paymentRepositoryPort.findByTxnRef(txnRef)
                    .orElse(null);

            if (payment == null) {
                return ipnResponse("01", "Order not Found");
            }

            if (!isValidAmount(payment, servletRequest.getParameter("vnp_Amount"))) {
                return ipnResponse("04", "Invalid Amount");
            }
            String responseCode = servletRequest.getParameter("vnp_ResponseCode");
            if (PaymentStatus.PAID.equals(payment.getStatus()) && "00".equals(responseCode)) {
                return ipnResponse("00", "Confirm Success");
            }
            if (!PaymentStatus.PENDING.equals(payment.getStatus())) {
                return ipnResponse("02", "Order already confirmed");
            }

            OffsetDateTime confirmedAt = OffsetDateTime.now();
            if ("00".equals(responseCode)) {
                payment.markPaid(confirmedAt);
                PaymentAggregate paidPayment = paymentRepositoryPort.save(payment);
                publishPaymentSucceededForBookings(paidPayment);
                return ipnResponse("00", "Confirm Success");
            }
            String failureReason = "VNPAY payment failed with response code: " + responseCode;
            payment.markFailed(confirmedAt, failureReason);
            PaymentAggregate failedPayment = paymentRepositoryPort.save(payment);
            publishPaymentFailedForBookings(failedPayment, failureReason);
            return ipnResponse("00", "Confirm Success");
        } catch (Exception ex) {
            return ipnResponse("99", "Unknown error");
        }
    }

    @Override
    public void returnUrl(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //Begin process return from VNPAY
        Map<String, String> fields = new HashMap<>();
        for (Enumeration<String> params = request.getParameterNames(); params.hasMoreElements();) {
            String fieldName = params.nextElement();
            String fieldValue = request.getParameter(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                fields.put(fieldName, fieldValue);
            }
        }

        String vnp_SecureHash = request.getParameter("vnp_SecureHash");
        if (fields.containsKey("vnp_SecureHashType")) {
            fields.remove("vnp_SecureHashType");
        }
        if (fields.containsKey("vnp_SecureHash")) {
            fields.remove("vnp_SecureHash");
        }

        sLog.info("Fields: {}", fields);
        String signValue = vnPayUtils.hashAllFields(vnPayProperties.hashSecret(), fields);

        sLog.info("Sign value: {}", signValue);
        String frontendRedirectUrl = "http://localhost:5173/payment-result"; // Link về app frontend của bạn

        if (signValue.equalsIgnoreCase(vnp_SecureHash)) {
            if ("00".equals(request.getParameter("vnp_ResponseCode"))) {
                response.sendRedirect(frontendRedirectUrl + "?status=success&txnRef=" + request.getParameter("vnp_TxnRef"));
            } else {
                response.sendRedirect(frontendRedirectUrl + "?status=failed&txnRef=" + request.getParameter("vnp_TxnRef"));
            }

        } else {
            sLog.warn("[VNPAY-RETURN] Invalid checksum hashData={} signValue={} vnpSecureHash={} fields={}",
                    vnPayUtils.buildHashData(fields), signValue, vnp_SecureHash, fields);
            response.sendRedirect(frontendRedirectUrl + "?status=invalid_signature");
        }
    }

    private Map<String, String> collectEncodedFields(HttpServletRequest servletRequest) {
        Map<String, String> fields = new HashMap<>();
        for (Enumeration<String> params = servletRequest.getParameterNames(); params.hasMoreElements(); ) {
            String paramName = params.nextElement();
            String paramValue = servletRequest.getParameter(paramName);
            if (paramValue == null || paramValue.isEmpty()) {
                continue;
            }
            fields.put(paramName, paramValue);
        }
        return fields;
    }

    private String buildRawHashData(String rawQueryString) {
        if (!hasText(rawQueryString)) {
            return "";
        }

        Map<String, String> fields = new HashMap<>();
        for (String param : rawQueryString.split("&")) {
            int separatorIndex = param.indexOf('=');
            String fieldName = separatorIndex >= 0 ? param.substring(0, separatorIndex) : param;
            String fieldValue = separatorIndex >= 0 ? param.substring(separatorIndex + 1) : "";
            if (!hasText(fieldName)
                    || !hasText(fieldValue)
                    || "vnp_SecureHash".equals(fieldName)
                    || "vnp_SecureHashType".equals(fieldName)) {
                continue;
            }
            fields.put(fieldName, fieldValue);
        }

        List<String> fieldNames = new ArrayList<>(fields.keySet());
        Collections.sort(fieldNames);
        StringBuilder sb = new StringBuilder();
        Iterator<String> itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = itr.next();
            sb.append(fieldName).append('=').append(fields.get(fieldName));
            if (itr.hasNext()) {
                sb.append('&');
            }
        }
        return sb.toString();
    }

    private boolean matchesSecureHash(String signValue, String secureHash) {
        return hasText(signValue) && hasText(secureHash) && signValue.equalsIgnoreCase(secureHash);
    }

    private boolean isValidAmount(PaymentAggregate payment, String amountParam) {
        if (!hasText(amountParam)) {
            return false;
        }
        try {
            BigDecimal returnedAmount = new BigDecimal(amountParam.trim());
            BigDecimal expectedAmount = payment.getAmount().multiply(BigDecimal.valueOf(100));
            return expectedAmount.compareTo(returnedAmount) == 0;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    private void publishPaymentSucceededForBookings(PaymentAggregate payment) {
        resolveBookingCodes(payment).forEach(bookingCode ->
                paymentEventPublisherPort.publishPaymentSucceeded(buildMetadata(), paymentForBooking(payment, bookingCode))
        );
    }

    private void publishPaymentFailedForBookings(PaymentAggregate payment, String failureReason) {
        resolveBookingCodes(payment).forEach(bookingCode ->
                paymentEventPublisherPort.publishPaymentFailed(buildMetadata(), paymentForBooking(payment, bookingCode), failureReason)
        );
    }

    private List<String> resolveBookingCodes(PaymentAggregate payment) {
        List<String> bookingCodes = paymentRepositoryPort.findBookingCodesByPaymentId(payment.getId());
        if (!bookingCodes.isEmpty()) {
            return bookingCodes;
        }
        return List.of(payment.getBookingCode());
    }

    private PaymentAggregate paymentForBooking(PaymentAggregate payment, String bookingCode) {
        return PaymentAggregate.builder()
                .id(payment.getId())
                .bookingCode(bookingCode)
                .method(payment.getMethod())
                .txnRef(payment.getTxnRef())
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .status(payment.getStatus())
                .paidAt(payment.getPaidAt())
                .failedAt(payment.getFailedAt())
                .failureReason(payment.getFailureReason())
                .description(payment.getDescription())
                .createdAt(payment.getCreatedAt())
                .createdBy(payment.getCreatedBy())
                .updatedAt(payment.getUpdatedAt())
                .updatedBy(payment.getUpdatedBy())
                .build();
    }

    private RequestContext buildMetadata() {
        String now = OffsetDateTime.now().toString();
        return RequestContext.builder()
                .requestId(UUID.randomUUID().toString())
                .requestDateTime(now)
                .channel("ONL")
                .build();
    }

    private VNPayIpnResponse ipnResponse(String rspCode, String message) {
        return VNPayIpnResponse.builder()
                .rspCode(rspCode)
                .message(message)
                .build();
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
