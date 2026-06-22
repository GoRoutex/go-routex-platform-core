package platform.booking.service.infrastructure.kafka.consumer;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import platform.booking.service.application.handler.impl.PaymentEventHandler;
import platform.booking.service.infrastructure.persistence.utils.JsonUtils;
import platform.core.common.service.api.BaseRequest;
import platform.core.common.service.infrastructure.event.DomainEvent;
import platform.core.common.service.infrastructure.kafka.event.PaymentFailedEvent;
import platform.core.common.service.persistence.exception.BusinessException;
import platform.core.common.service.persistence.utils.ExceptionUtils;
import vn.com.go.routex.identity.security.log.SystemLog;

import static platform.core.common.service.persistence.constant.ErrorConstant.INVALID_DATA_ERROR_MESSAGE;
import static platform.core.common.service.persistence.constant.ErrorConstant.INVALID_INPUT_ERROR;

@Component
@Lazy(false)
@RequiredArgsConstructor
public class PaymentFailedEventConsumer {


    @Value("${spring.kafka.events.payment-failed}")
    private String paymentFailedEvent;

    private final PaymentEventHandler paymentEventHandler;
    private final SystemLog sLog = SystemLog.getLogger(this.getClass());

    @KafkaListener(
            topics = "${spring.kafka.topics.payments}",
            containerFactory = "kafkaListenerContainerFactory",
            groupId = "${spring.kafka.group-id.payment-failed}"
    )
    public void paymentFailedConsumer(String payload, Acknowledgment acknowledgment) {
        DomainEvent event =
                JsonUtils.parseToKafkaObject(
                        payload,
                        new TypeReference<>() {
                        });

        if (!paymentFailedEvent.equals(event.eventType())) {
            sLog.info("Ignore event {}", event.eventType());
            acknowledgment.acknowledge();
            return;
        }

        BaseRequest context = JsonUtils.convertValue(event.header().get("context"), BaseRequest.class);
        PaymentFailedEvent paymentEvent = JsonUtils.convertValue(event.payload().get("data"), PaymentFailedEvent.class);

        sLog.info("[PAYMENT-EVENT] Processing event: eventName={} eventId={} aggregateId={} paymentId={}",
                event.eventType(),
                event.eventId(),
                event.aggregateId(),
                paymentEvent.paymentId());

        try {

            validateEvent(event, context, paymentEvent);
            paymentEventHandler.updateFailEvent(event, context, paymentEvent);
            sLog.info("[BOOKING-EVENT] Event processed successfully: eventName={} eventId={} paymentId={}", event.eventType(), event.eventId(), event.aggregateId());
            acknowledgment.acknowledge();
        } catch(Exception ex) {
            sLog.error("[PAYMENT-EVENT] Failed eventName={} eventId={} aggregateId={} paymentId={} bookingCode={}",
                    event.eventType(),
                    event.eventId(),
                    event.aggregateId(),
                    paymentEvent.paymentId(),
                    paymentEvent.bookingCode(),
                    ex);
            throw ex;
        }
        // Publish event for notification
        // Publish event for analytics
    }

    public void validateEvent(DomainEvent event, BaseRequest context, PaymentFailedEvent data) {
        if (event == null
                || event.header() == null
                || event.payload() == null
                || event.header().get("context") == null
                || event.payload().get("data") == null) {
            throw new BusinessException(ExceptionUtils.buildResultResponse(INVALID_INPUT_ERROR, INVALID_DATA_ERROR_MESSAGE));
        }
    }
}
