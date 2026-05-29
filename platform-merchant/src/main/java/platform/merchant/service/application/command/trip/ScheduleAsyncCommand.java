package platform.merchant.service.application.command.trip;

import lombok.Builder;
import platform.core.common.service.common.RequestContext;

import java.util.List;

@Builder
public record ScheduleAsyncCommand(
        String merchantId,
        String routeId,
        RequestContext context,
        List<DemandEntry> demands,
        List<Integer> operatingHours,
        double operatingCostPerTrip,
        int maxTripsAllowed,
        double minLoadFactor
) {
    @Builder
    public record DemandEntry(String date, double demand) {}
}
