package platform.core.common.service.infrastructure.kafka.event;


import lombok.Builder;
import platform.core.common.service.domain.booking.PaymentStatus;

import java.math.BigDecimal;

@Builder
public record PaymentSuccessEvent(
        String paymentId,
        String customerId,
        String bookingCode,
        BigDecimal amount,
        PaymentStatus status,
        String currency
) {
}
