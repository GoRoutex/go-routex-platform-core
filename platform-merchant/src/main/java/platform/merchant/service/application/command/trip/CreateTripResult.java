package platform.merchant.service.application.command.trip;

import lombok.Builder;
import platform.core.common.service.domain.trip.TripStatus;

import java.time.OffsetDateTime;

@Builder
public record CreateTripResult(
        String tripId,
        String routeId,
        String merchantId,
        OffsetDateTime departureTime,
        String rawDepartureTime,
        String rawDepartureDate,
        TripStatus status
) {
}
