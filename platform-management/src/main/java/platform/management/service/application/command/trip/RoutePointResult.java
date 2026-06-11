package platform.management.service.application.command.trip;

import lombok.Builder;

@Builder
public record RoutePointResult(
        String id,
        int stopOrder,
        String routeId,
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
