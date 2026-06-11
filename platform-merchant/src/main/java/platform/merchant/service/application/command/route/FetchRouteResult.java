package platform.merchant.service.application.command.route;

import lombok.Builder;
import platform.merchant.service.domain.route.RouteStatus;

import java.util.List;

@Builder
public record FetchRouteResult(
        String id,
        String creator,
        String originCode,
        String originName,
        String destinationCode,
        String destinationName,
        String originDepartmentId,
        String originDepartmentName,
        String destinationDepartmentId,
        String destinationDepartmentName,
        Long duration,
        RouteStatus status,
        List<RoutePointResult> routePoints
) {
}
