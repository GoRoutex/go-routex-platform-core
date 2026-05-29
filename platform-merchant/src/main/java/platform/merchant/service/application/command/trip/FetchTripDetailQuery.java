package platform.merchant.service.application.command.trip;

import lombok.Builder;
import platform.core.common.service.common.RequestContext;
import platform.core.common.service.domain.trip.TripStatus;

@Builder
public record FetchTripDetailQuery(
        RequestContext context,
        String tripId,
        String merchantId,
        TripStatus status
) {
}
