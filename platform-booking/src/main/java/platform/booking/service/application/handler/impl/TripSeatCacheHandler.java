package platform.booking.service.application.handler.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import platform.booking.service.application.handler.TripSeatCacheEvent;
import platform.core.common.service.infrastructure.kafka.event.TripSeatGeneratedEvent;
import platform.core.common.service.infrastructure.redis.service.TripSeatCacheService;


@Component
@RequiredArgsConstructor
public class TripSeatCacheHandler implements TripSeatCacheEvent {

    private final TripSeatCacheService tripSeatCacheService;

    @Override
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleTripSeatGenerated(TripSeatGeneratedEvent event) {
        tripSeatCacheService.putSeats(event.tripId(), event.seats());
    }
}
