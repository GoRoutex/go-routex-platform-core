package platform.core.common.service.infrastructure.kafka.event;

import platform.core.common.service.infrastructure.redis.models.TripCacheSeat;

import java.util.List;

public record TripSeatGeneratedEvent(
        String tripId,
        List<TripCacheSeat> seats
) {
}