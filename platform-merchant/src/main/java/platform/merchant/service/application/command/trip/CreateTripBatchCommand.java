package platform.merchant.service.application.command.trip;

import lombok.Builder;
import platform.core.common.service.common.RequestContext;

import java.time.OffsetDateTime;
import java.util.List;

@Builder
public record CreateTripBatchCommand(
        RequestContext context,
        String routeId,
        String merchantId,
        List<TripBatchCommandData> trips
) {
    @Builder
    public record TripBatchCommandData(
            OffsetDateTime departureTime,
            String rawDepartureTime,
            String rawDepartureDate
    ) {}
}
