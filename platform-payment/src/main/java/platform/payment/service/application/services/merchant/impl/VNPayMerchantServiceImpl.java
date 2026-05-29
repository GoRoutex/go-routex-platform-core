package platform.payment.service.application.services.merchant.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.com.go.routex.identity.security.log.SystemLog;
import platform.payment.service.application.command.payment.GetPaymentUrlCommand;
import platform.payment.service.application.command.payment.GetPaymentUrlResult;
import platform.payment.service.application.services.VNPayService;
import platform.payment.service.application.services.merchant.PaymentMerchantService;
import platform.core.common.service.domain.booking.BookingStatus;
import platform.core.common.service.domain.booking.PaymentStatus;
import platform.payment.service.domain.booking.model.BookingPaymentContext;
import platform.payment.service.domain.booking.port.BookingPaymentQueryPort;
import platform.payment.service.domain.merchant.MerchantSessionStatus;
import platform.payment.service.domain.merchant.model.MerchantSessionAggregate;
import platform.payment.service.domain.merchant.port.MerchantSessionRepositoryPort;
import platform.core.common.service.domain.payment.PaymentMethod;
import platform.core.common.service.domain.payment.model.PaymentAggregate;
import platform.payment.service.domain.payment.port.PaymentRepositoryPort;
import platform.payment.service.domain.payment.port.QrCodeGeneratorPort;
import platform.core.common.service.persistence.exception.BusinessException;
import platform.core.common.service.persistence.utils.ExceptionUtils;

import java.time.OffsetDateTime;
import java.util.UUID;

import static platform.core.common.service.persistence.constant.ErrorConstant.INVALID_DATA_ERROR;

@Service
@RequiredArgsConstructor
public class VNPayMerchantServiceImpl implements PaymentMerchantService {

    private final BookingPaymentQueryPort bookingPaymentQueryPort;
    private final QrCodeGeneratorPort qrCodeGeneratorPort;
    private final PaymentRepositoryPort paymentRepositoryPort;
    private final MerchantSessionRepositoryPort merchantSessionRepositoryPort;
    private final VNPayService vnPayService;

    private final SystemLog sLog = SystemLog.getLogger(this.getClass());

    @Override
    public PaymentMethod getPaymentMethod() {
        return PaymentMethod.VNPAY;
    }

    @Override
    public GetPaymentUrlResult getPaymentUrl(GetPaymentUrlCommand command) {

        BookingPaymentContext bookingAggregate = bookingPaymentQueryPort.getBookingPaymentContext(command.bookingCode(), command.context());
        if (!BookingStatus.PENDING_PAYMENT.equals(bookingAggregate.getBookingStatus())) {
            throw new BusinessException(command.context().requestId(), command.context().requestDateTime(), command.context().channel(),
                    ExceptionUtils.buildResultResponse(INVALID_DATA_ERROR, "Booking Status is not Pending Payment"));
        }
        if (!bookingAggregate.getHoldUntil().isAfter(OffsetDateTime.now())) {
            throw new BusinessException(command.context().requestId(), command.context().requestDateTime(), command.context().channel(),
                    ExceptionUtils.buildResultResponse(INVALID_DATA_ERROR, "Booking Session is expired"));
        }

        if (command.amount().compareTo(bookingAggregate.getTotalAmount()) != 0) {
            throw new BusinessException(command.context().requestId(), command.context().requestDateTime(), command.context().channel(),
                    ExceptionUtils.buildResultResponse(INVALID_DATA_ERROR, "Payment amount does not match booking total amount"));
        }
        OffsetDateTime now = OffsetDateTime.now();
        PaymentAggregate payment = getOrCreatePendingPayment(command, bookingAggregate, now);

        sLog.info("Payment aggregate: {}", payment);
        String checkoutUrl = buildCheckoutUrl(command, payment.getTxnRef());
        MerchantSessionAggregate session = getOrCreateReusableMerchantSession(command, payment, bookingAggregate, checkoutUrl, now);
        String qrCodeUrl = qrCodeGeneratorPort.generateBase64Png(
                checkoutUrl,
                300,
                300
        );
        return GetPaymentUrlResult.builder()
                .bookingCode(command.bookingCode())
                .amount(payment.getAmount())
                .method(command.method())
                .qrCodeUrl(qrCodeUrl)
                .paymentUrl(checkoutUrl)
                .deeplink(session.getDeeplink())
                .expiredTime(session.getExpiredAt())
                .build();
    }

    private String buildCheckoutUrl(GetPaymentUrlCommand command, String txnRef) {
        try {
            return vnPayService.createPaymentUrl(command, txnRef);
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to create VNPay checkout url", ex);
        }
    }

    private MerchantSessionAggregate getOrCreateReusableMerchantSession(
            GetPaymentUrlCommand command,
            PaymentAggregate aggregate,
            BookingPaymentContext booking,
            String checkoutUrl,
            OffsetDateTime now
    ) {
        return merchantSessionRepositoryPort.findLatestByPaymentIdAndStatus(aggregate.getId(), MerchantSessionStatus.CREATED)
                .filter(session -> session.isReusable(now))
                .orElseGet(() -> {
                    int nextAttemptNo = merchantSessionRepositoryPort.countByPaymentId(aggregate.getId()) + 1;
                    MerchantSessionAggregate session = MerchantSessionAggregate.builder()
                            .id(UUID.randomUUID().toString())
                            .paymentId(aggregate.getId())
                            .paymentMerchant(command.method())
                            .status(MerchantSessionStatus.CREATED)
                            .attemptNo(nextAttemptNo)
                            .merchantTxnRef(aggregate.getId())
                            .checkoutUrl(checkoutUrl)
                            .deeplink("")
                            .expiredAt(booking.getHoldUntil())
                            .createdAt(now)
                            .build();
                    merchantSessionRepositoryPort.save(session);
                    return session;
                });
    }

    private PaymentAggregate getOrCreatePendingPayment(
            GetPaymentUrlCommand command,
            BookingPaymentContext booking,
            OffsetDateTime now
    ) {
        return paymentRepositoryPort
                .findByBookingCodeAndMethodAndStatus(
                        command.bookingCode(),
                        command.method(),
                        PaymentStatus.PENDING
                )
                .filter(payment -> payment.isReusablePendingPayment(now))
                .orElseGet(() -> {
                    PaymentAggregate payment = PaymentAggregate.builder()
                            .id(UUID.randomUUID().toString())
                            .bookingCode(command.bookingCode())
                            .method(command.method())
                            .amount(booking.getTotalAmount())
                            .txnRef(UUID.randomUUID().toString())
                            .currency(booking.getCurrency())
                            .status(PaymentStatus.PENDING)
                            .createdAt(now)
                            .build();

                    return paymentRepositoryPort.save(payment);
                });
    }
}
