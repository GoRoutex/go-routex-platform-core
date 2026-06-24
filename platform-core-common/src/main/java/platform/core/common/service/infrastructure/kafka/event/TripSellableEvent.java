package platform.core.common.service.infrastructure.kafka.event;

import lombok.Builder;
import platform.core.common.service.domain.trip.TripStatus;

import java.time.OffsetDateTime;

@Builder
public record TripSellableEvent(
        String tripId,
        String routeId,
        String vehicleId,
        String driverId,
        String assignedBy,
        OffsetDateTime assignedAt,
        TripStatus status,
        Long seatCount,
        String creator,
        Boolean hasFloor
) {
}
