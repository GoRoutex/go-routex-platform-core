package platform.driver.service.application.dto.manifest;

import lombok.Builder;

@Builder
public record GetTripManifestSummaryView(
        Integer totalSeats,
        Integer bookedSeats,
        Integer checkedInSeats,
        Integer boardedSeats,
        Integer cancelledSeats,
        Integer availableSeats
) {
}
