package platform.merchant.service.application.command.route;

import lombok.Builder;

import java.time.OffsetDateTime;
import java.util.List;

@Builder
public record SearchRouteItemResult(
        String id,
        String pickupBranch,
        String origin,
        String destination,
        Long availableSeats,
        OffsetDateTime plannedStartTime,
        OffsetDateTime plannedEndTime,
        String vehiclePlate,
        boolean hasFloor,
        String routeCode,
        List<RoutePointResult> routePoints
) {
}
