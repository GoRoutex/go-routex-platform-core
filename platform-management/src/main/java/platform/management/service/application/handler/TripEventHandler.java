package platform.management.service.application.handler;

import platform.core.common.service.api.BaseRequest;
import platform.core.common.service.infrastructure.event.DomainEvent;
import platform.core.common.service.infrastructure.kafka.event.TripAssignedEvent;

public interface TripEventHandler {

    void processAssignedEvent(DomainEvent event, BaseRequest context, TripAssignedEvent assignedEvent);
}
