package platform.management.service.infrastructure.kafka.consumer;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import platform.core.common.service.infrastructure.event.DomainEvent;
import platform.core.common.service.persistence.exception.BusinessException;
import platform.core.common.service.infrastructure.kafka.event.TripAssignedEvent;
import platform.core.common.service.persistence.utils.ExceptionUtils;
import vn.com.go.routex.identity.security.log.SystemLog;
import platform.management.service.application.handler.TripEventHandler;
import platform.management.service.infrastructure.persistence.utils.JsonUtils;
import platform.core.common.service.api.BaseRequest;

import static platform.core.common.service.persistence.constant.ErrorConstant.INVALID_DATA_ERROR_MESSAGE;
import static platform.core.common.service.persistence.constant.ErrorConstant.INVALID_EVENT_MESSAGE;
import static platform.core.common.service.persistence.constant.ErrorConstant.INVALID_INPUT_ERROR;

@RequiredArgsConstructor
@Component
public class TripAssignedConsumer {


    @Value("${spring.kafka.events.trip-assigned}")
    private String tripAssignedEvent;

    private final TripEventHandler tripEventHandler;

    private final SystemLog sLog = SystemLog.getLogger(this.getClass());

    @KafkaListener(
            topics = "${spring.kafka.topics.trips}",
            containerFactory = "kafkaListenerContainerFactory",
            groupId = "${spring.kafka.group-id.trips}"
    )
    public void routeAssignedConsumer(String payload, Acknowledgment acknowledgment) {
        sLog.info("[TRIP-ASSIGNED] Raw Payload: {}", payload);

        DomainEvent event = JsonUtils.parseToKafkaObject(
                payload,
                new TypeReference<>() {});


        sLog.info("[TRIP-ASSIGNED] Domain Event: {}", event);

        if(event == null ||
                event.header() == null ||
                event.payload() == null ||
                event.header().get("context") == null ||
                event.payload().get("data") == null) {
            throw new BusinessException(ExceptionUtils.buildResultResponse(INVALID_INPUT_ERROR, INVALID_DATA_ERROR_MESSAGE));
        }

        // double check if the event type is matched with the expectation
        if(!tripAssignedEvent.equals(event.eventType())) {
            sLog.info("Ignore Event: {}", event.eventType());
            acknowledgment.acknowledge();
            return;
        }
        BaseRequest context = JsonUtils.convertValue(event.header().get("context"), BaseRequest.class);
        TripAssignedEvent tripEvent = JsonUtils.convertValue(event.payload().get("data"), TripAssignedEvent.class);

        sLog.info("[TRIP-ASSIGNED] Processing event: eventType={} eventId={} aggregateId={} tripId={} vehicleId={} driverId={}",
                event.eventType(),
                event.eventId(),
                event.aggregateId(),
                tripEvent.tripId(),
                tripEvent.vehicleId(),
                tripEvent.driverId());

        sLog.info("[TRIP-ASSIGNED] Trip Assigned Event: {}", tripEvent);

        try {
            validateEvent(event, context, tripEvent);
            tripEventHandler.processAssignedEvent(event, context, tripEvent);
        } catch(Exception e) {
            sLog.error("[TRIP-EVENT] Failed eventName={} eventId={} aggregateId={} tripId={} vehicleId={}",
                    event.eventType(),
                    event.eventId(),
                    event.aggregateId(),
                    tripEvent.tripId(),
                    tripEvent.vehicleId(),
                    e);
            acknowledgment.acknowledge();
        }

        acknowledgment.acknowledge();
    }


    public void validateEvent(DomainEvent event, BaseRequest context, TripAssignedEvent data) {
        if (event.eventId().isBlank()
                || event.eventType().isBlank()
                || event.aggregateId().isBlank()
                || context == null
                || context.getRequestId().isBlank()
                || context.getRequestDateTime().isBlank()
                || context.getChannel().isBlank()
                || data.tripId().isBlank()
                || data.vehicleId().isBlank()
                || data.driverId().isBlank()) {
            throw new BusinessException(ExceptionUtils.buildResultResponse(INVALID_INPUT_ERROR, String.format(INVALID_EVENT_MESSAGE, event.eventType())));
        }
    }
}
