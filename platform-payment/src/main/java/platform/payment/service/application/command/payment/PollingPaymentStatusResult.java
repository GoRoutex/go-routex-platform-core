package platform.payment.service.application.command.payment;

import lombok.Builder;
import platform.core.common.service.domain.booking.PaymentStatus;

import java.math.BigDecimal;

@Builder
public record PollingPaymentStatusResult(
        String bookingCode,
        BigDecimal amount,
        PaymentStatus status,
        boolean shouldStopPooling
) {
}
