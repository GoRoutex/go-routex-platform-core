package platform.core.common.service.infrastructure.kafka.event;

import lombok.Builder;
import platform.core.common.service.domain.booking.PaymentStatus;

@Builder
public record PaymentFailedEvent(
        String paymentId,
        String bookingCode,
        PaymentStatus status,
        String reason
) {
}
