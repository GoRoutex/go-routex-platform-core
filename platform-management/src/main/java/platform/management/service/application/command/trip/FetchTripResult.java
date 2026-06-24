package platform.management.service.application.command.trip;

import lombok.Builder;
import platform.core.common.service.domain.trip.TripStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Builder
public record FetchTripResult(
        String id,
        String creator,
        String tripCode,
        String merchantId,
        String merchantName,
        String merchantDisplayName,
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
        String driverId,
        String driverName,
        Boolean hasFloor,
        OffsetDateTime assignedAt,
        BigDecimal ticketPrice,
        Long availableSeat,
        List<RoutePointResult> routePoints
) {
}
