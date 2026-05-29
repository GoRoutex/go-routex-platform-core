package platform.core.common.service.api;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Builder
public record BookingPaymentContextResponse(
        String bookingId,
        String bookingCode,
        BigDecimal totalAmount,
        String currency,
        String bookingStatus,
        OffsetDateTime holdUntil
) {
}
