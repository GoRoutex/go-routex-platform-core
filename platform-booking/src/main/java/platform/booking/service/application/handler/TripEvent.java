package platform.booking.service.application.handler;

import platform.core.common.service.infrastructure.event.DomainEvent;
import platform.core.common.service.infrastructure.kafka.event.TripSellableEvent;
import platform.core.common.service.api.BaseRequest;

public interface TripEvent {

    void generateRouteSeat(DomainEvent event, BaseRequest context, TripSellableEvent payload);
}
