package platform.payment.service.application.command.payment;

import lombok.Builder;
import platform.core.common.service.domain.payment.PaymentMethod;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Builder
public record GetPaymentUrlResult(
        String bookingCode,
        BigDecimal amount,
        PaymentMethod method,
        String qrCodeUrl,
        String paymentUrl,
        String deeplink,
        OffsetDateTime expiredTime
) {
}
