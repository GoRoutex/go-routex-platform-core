package platform.merchant.service.application.command.trip;

import lombok.Builder;
import platform.core.common.service.domain.trip.TripStatus;

@Builder
public record DeleteTripResult(
        String tripId,
        TripStatus status
) {
}
