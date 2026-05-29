package platform.merchant.service.application.command.route;

import lombok.Builder;

@Builder
public record RoutePointCommand(
        String stopOrder,
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
