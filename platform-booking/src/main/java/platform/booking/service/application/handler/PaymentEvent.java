package platform.booking.service.application.handler;

import platform.core.common.service.infrastructure.event.DomainEvent;
import platform.core.common.service.api.BaseRequest;
import platform.core.common.service.infrastructure.kafka.event.PaymentFailedEvent;
import platform.core.common.service.infrastructure.kafka.event.PaymentSuccessEvent;

public interface PaymentEvent {
    void updateSuccessPayment(DomainEvent event, BaseRequest context, PaymentSuccessEvent payload);
    void updateFailEvent(DomainEvent event, BaseRequest context, PaymentFailedEvent payload);
}
