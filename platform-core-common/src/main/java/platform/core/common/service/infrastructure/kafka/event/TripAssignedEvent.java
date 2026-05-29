package platform.core.common.service.infrastructure.kafka.event;

import lombok.Builder;
import platform.core.common.service.domain.trip.TripStatus;

import java.time.OffsetDateTime;

@Builder
public record TripAssignedEvent(
        String tripId,
        String vehicleId,
        String driverId,
        OffsetDateTime departureTime,
        String originName,
        String destinationName,
        String assignedBy,
        OffsetDateTime assignedAt,
        TripStatus status
) {
}
