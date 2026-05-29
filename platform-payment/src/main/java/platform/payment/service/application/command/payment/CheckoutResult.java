package platform.payment.service.application.command.payment;

import lombok.Builder;
import platform.core.common.service.domain.booking.PaymentStatus;
import platform.core.common.service.api.ApiResult;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Builder
public record CheckoutResult(
        ApiResult result,
        String paymentId,
        PaymentStatus status,
        BigDecimal amount,
        String currency,
        OffsetDateTime paidAt
) {
}
