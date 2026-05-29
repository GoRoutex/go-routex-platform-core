package platform.driver.service.application.dto.manifest;

import lombok.Builder;

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
        String dropOffPointName
) {
}
