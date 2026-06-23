package platform.booking.service.application.handler;

import platform.core.common.service.api.BaseRequest;
import platform.core.common.service.infrastructure.event.DomainEvent;
import platform.core.common.service.infrastructure.kafka.event.TripSellableEvent;

public interface TripEvent {

    void generateTripSeat(DomainEvent event, BaseRequest context, TripSellableEvent payload);
}
