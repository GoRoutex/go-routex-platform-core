package platform.booking.service.application.handler;

import platform.core.common.service.infrastructure.kafka.event.TripSeatGeneratedEvent;

public interface TripSeatCacheEvent {
    void handleTripSeatGenerated(TripSeatGeneratedEvent event);
}
