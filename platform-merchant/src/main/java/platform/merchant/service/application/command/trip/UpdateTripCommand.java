package platform.merchant.service.application.command.trip;

import lombok.Builder;
import platform.core.common.service.common.RequestContext;

import java.time.OffsetDateTime;

@Builder
public record UpdateTripCommand(
        RequestContext context,
        String tripId,
        String routeId,
        String merchantId,
        OffsetDateTime departureTime,
        String pickupBranch,
        String rawDepartureTime,
        String rawDepartureDate
) {
}
