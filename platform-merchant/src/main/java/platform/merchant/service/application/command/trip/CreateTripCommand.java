package platform.merchant.service.application.command.trip;

import lombok.Builder;
import platform.core.common.service.common.RequestContext;

import java.time.OffsetDateTime;

@Builder
public record CreateTripCommand(
        RequestContext context,
        String routeId,
        String merchantId,
        OffsetDateTime departureTime,
        String rawDepartureTime,
        String rawDepartureDate
) {
}
