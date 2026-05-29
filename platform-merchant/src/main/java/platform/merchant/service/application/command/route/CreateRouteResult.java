package platform.merchant.service.application.command.route;

import lombok.Builder;
import platform.merchant.service.domain.route.RouteStatus;

import java.util.List;

@Builder
public record CreateRouteResult(
        String id,
        String creator,
        String originCode,
        String originName,
        String destinationCode,
        String destinationName,
        String originProvinceId,
        String destinationProvinceId,
        String originDepartmentId,
        String originDepartmentName,
        String destinationDepartmentId,
        String destinationDepartmentName,
        RouteStatus status,
        Long duration,
        List<RoutePointCommand> routePoints
) {
}
