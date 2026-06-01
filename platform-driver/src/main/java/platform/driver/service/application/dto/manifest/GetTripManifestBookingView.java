package platform.driver.service.application.dto.manifest;

import lombok.Builder;
import platform.core.common.service.domain.ticket.TicketStatus;

import java.time.OffsetDateTime;

@Builder
public record GetTripManifestBookingView(
        String bookingId,
        String bookingCode,
        String ticketId,
        String passengerName,
        String passengerPhoneNumber,
        String seatNumber,
        String pickupPointId,
        String pickupPointName,
        String dropOffPointId,
        String dropOffPointName,
        TicketStatus status,
        OffsetDateTime checkedInAt,
        String checkedInBy,
        OffsetDateTime boardedAt,
        String boardedBy
) {
}
