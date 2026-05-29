package platform.merchant.service.application.command.route;


import lombok.Builder;
import platform.merchant.service.domain.route.RouteStatus;

import java.util.List;

@Builder
public record UpdateRouteResult(
        String routeId,
        String creator,
        String originCode,
        String originName,
        String originDepartmentId,
        String destinationCode,
        String destinationName,
        String destinationDepartmentId,
        Long duration,
        RouteStatus status,
        List<UpdateRoutePointResult> routePoints
) {
    @Builder
    public record UpdateRoutePointResult(
            int stopOrder,
            String note,
            String departmentId,
            String stopName,
            String stopAddress,
            String stopCity,
            Double stopLatitude,
            Double stopLongitude,
            Integer timeAtDepartment
    ) {

    }
}
