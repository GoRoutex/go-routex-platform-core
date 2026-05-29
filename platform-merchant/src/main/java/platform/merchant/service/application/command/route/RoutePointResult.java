package platform.merchant.service.application.command.route;

import lombok.Builder;

import java.time.OffsetDateTime;

@Builder
public record RoutePointResult(
        String id,
        String routeId,
        String creator,
        Integer stopOrder,
        String note,
        String departmentId,
        String stopName,
        String stopAddress,
        String stopCity,
        Double stopLatitude,
        Double stopLongitude,
        Long stayDuration,
        Integer timeAtDepartment,
        OffsetDateTime createdAt,
        String createdBy
) {
}
