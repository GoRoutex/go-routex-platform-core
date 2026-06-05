package platform.payment.service.application.command.payment;

import lombok.Builder;
import platform.core.common.service.api.ApiResult;
import platform.core.common.service.domain.booking.PaymentStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Builder
public record CreatePaymentSessionResult(
        ApiResult result,
        String paymentId,
        String bookingId,
        BigDecimal amount,
        String currency,
        PaymentStatus status,
        String qrContent,
        String checkoutUrl,
        OffsetDateTime expiresAt
) {
}
