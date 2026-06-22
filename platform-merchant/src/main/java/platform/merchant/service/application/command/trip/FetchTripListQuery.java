package platform.merchant.service.application.command.trip;

import lombok.Builder;
import platform.core.common.service.common.RequestContext;
import platform.core.common.service.domain.trip.TripStatus;

@Builder
public record FetchTripListQuery(
        RequestContext context,
        TripStatus status,
        String rawDepartureDate,
        String period,
        String year,
        String month,
        String quarter,
        String pageSize,
        String pageNumber
) {
}
