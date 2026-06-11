package platform.management.service.application.command.trip;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Builder
public record SearchTripItemResult(
        String id,
        String merchantId,
        String vehicleId,
        String routeId,
        String driverId,
        String merchantName,
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
        Long availableSeats,
        BigDecimal ticketPrice,
        OffsetDateTime departureTime,
        String rawDepartureDate,
        String rawDepartureTime,
        Long durationMinutes,
        String vehiclePlate,
        boolean hasFloor,
        String tripCode,
        List<RoutePointResult> routePoints
) {
}
