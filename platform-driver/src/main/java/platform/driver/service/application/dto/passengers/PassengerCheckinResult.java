package platform.driver.service.application.dto.passengers;


import lombok.Builder;
import platform.core.common.service.domain.ticket.TicketStatus;

import java.time.OffsetDateTime;

@Builder
public record PassengerCheckinResult(
    String ticketCode,
    String customerName,
    String seatNumber,
    String tripId,
    TicketStatus status,
    OffsetDateTime checkedInAt,
    OffsetDateTime boardedAt
) {
}
