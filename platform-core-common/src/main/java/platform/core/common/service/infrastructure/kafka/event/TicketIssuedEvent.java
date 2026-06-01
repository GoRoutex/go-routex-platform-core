package platform.core.common.service.infrastructure.kafka.event;

import lombok.Builder;
import platform.core.common.service.domain.ticket.TicketStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Builder
public record TicketIssuedEvent(
        String bookingId,
        String bookingCode,
        String customerId,
        String customerName,
        String customerPhone,
        String customerEmail,
        String merchantId,
        String tripId,
        OffsetDateTime departureTime,
        BigDecimal totalAmount,
        String currency,
        OffsetDateTime paidAt,
        List<TicketIssuedItem> tickets
) {
    @Builder
    public record TicketIssuedItem(
            String ticketId,
            String ticketCode,
            String bookingSeatId,
            String seatNumber,
            BigDecimal price,
            TicketStatus status,
            OffsetDateTime issuedAt
    ) {
    }
}
