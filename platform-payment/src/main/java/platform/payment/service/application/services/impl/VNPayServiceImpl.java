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
import platform.payment.service.infrastructure.integration.constant.VNPayConstant;
import platform.payment.service.infrastructure.integration.utils.VNPayUtils;
import platform.payment.service.interfaces.model.vnpay.VNPayIpnResponse;
import vn.com.go.routex.identity.security.log.SystemLog;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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
    private final PaymentRepositoryPort paymentRepositoryPort;
    private final PaymentEventPublisherPort paymentEventPublisherPort;
    private final SystemLog sLog = SystemLog.getLogger(this.getClass());

    @Override
    public String createPaymentUrl(GetPaymentUrlCommand request, String txnRef) {
        String vnp_Version = "2.1.0";
        String vnp_Command = "pay";
        String orderType = "other";
        BigDecimal amount = request.amount().multiply(BigDecimal.valueOf(100));

        String bankCode = request.bankCode();
        String vnp_IpAddr = hasText(request.clientIp()) ? request.clientIp().trim() : "127.0.0.1";

        String vnp_TmnCode = VNPayConstant.vnp_TMNCODE;

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amount));
        vnp_Params.put("vnp_CurrCode", "VND");

        if (bankCode != null && !bankCode.isEmpty()) {
            vnp_Params.put("vnp_BankCode", bankCode);
        }

        vnp_Params.put("vnp_TxnRef", txnRef);
        vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang:" + request.bookingCode());
        vnp_Params.put("vnp_OrderType", orderType);
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", VNPayConstant.vnp_RETURNURL);
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();

        Iterator<String> itr = fieldNames.iterator();
        while (itr.hasNext()) {

            String fieldName = itr.next();
            String fieldValue = vnp_Params.get(fieldName);

            if ((fieldValue != null) && (!fieldValue.isEmpty())) {
                //Build hash data
                hashData.append(fieldName)
                        .append('=')
                        .append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                //Build query
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII))
                        .append('=')
                        .append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }
        String queryUrl = query.toString();
        String vnp_SecureHash = vnPayUtils.hmacSHA512(VNPayConstant.SECRET_KEY, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;

        sLog.info("Secure hash: {}", vnp_SecureHash);
        return VNPayConstant.vnp_PAYURL + "?" + queryUrl;
    }

    @Override
    public VNPayIpnResponse processIpn(HttpServletRequest servletRequest) {
        try {
            Map<String, String> fields = collectEncodedFields(servletRequest);
            String vnpSecureHash = servletRequest.getParameter("vnp_SecureHash");

            fields.remove("vnp_SecureHashType");
            fields.remove("vnp_SecureHash");

            String signValue = vnPayUtils.hashAllFields(fields);
            if (!signValue.equals(vnpSecureHash)) {
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

            if ("00".equals(responseCode)) {
                paymentEventPublisherPort.publishPaymentSucceeded(buildMetadata(), payment);
                return ipnResponse("00", "Confirm Success");
            }
            String failureReason = "VNPAY payment failed with response code: " + responseCode;
            paymentEventPublisherPort.publishPaymentFailed(buildMetadata(), payment, failureReason);
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
        String signValue = vnPayUtils.hashAllFields(fields);

        sLog.info("Sign value: {}", signValue);
        String frontendRedirectUrl = "http://localhost:5173/payment-result"; // Link về app frontend của bạn

        if (signValue.equals(vnp_SecureHash)) {
            if ("00".equals(request.getParameter("vnp_ResponseCode"))) {
                response.sendRedirect(frontendRedirectUrl + "?status=success&txnRef=" + request.getParameter("vnp_TxnRef"));
            } else {
                response.sendRedirect(frontendRedirectUrl + "?status=failed&txnRef=" + request.getParameter("vnp_TxnRef"));
            }

        } else {
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
            fields.put(
                    URLEncoder.encode(paramName, StandardCharsets.US_ASCII),
                    URLEncoder.encode(paramValue, StandardCharsets.US_ASCII)
            );
        }
        return fields;
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
