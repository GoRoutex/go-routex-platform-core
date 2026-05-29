package platform.management.service.application.command.route;

import lombok.Builder;
import platform.core.common.service.domain.trip.TripStatus;

import java.time.OffsetDateTime;
import java.util.List;

@Builder
public record FetchTripResult(
        String id,
        String creator,
        String tripCode,
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
        OffsetDateTime departureTime,
        String rawDepartureDate,
        String rawDepartureTime,
        Long durationMinutes,
        TripStatus status,
        String vehicleId,
        String vehiclePlate,
        Boolean hasFloor,
        OffsetDateTime assignedAt,
        List<RoutePointResult> routePoints
) {
}
