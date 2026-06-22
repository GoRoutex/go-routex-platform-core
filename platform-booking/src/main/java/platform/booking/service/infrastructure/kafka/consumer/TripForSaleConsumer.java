package platform.booking.service.infrastructure.kafka.consumer;


import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import platform.booking.service.application.handler.impl.TripEventHandler;
import platform.booking.service.infrastructure.persistence.utils.JsonUtils;
import platform.core.common.service.api.BaseRequest;
import platform.core.common.service.application.service.OutBoxService;
import platform.core.common.service.infrastructure.event.DomainEvent;
import platform.core.common.service.infrastructure.kafka.event.TripOpenForBookingEvent;
import platform.core.common.service.infrastructure.kafka.event.TripSellableEvent;
import platform.core.common.service.persistence.exception.BusinessException;
import platform.core.common.service.persistence.utils.ExceptionUtils;
import vn.com.go.routex.identity.security.log.SystemLog;

import static platform.core.common.service.persistence.constant.ErrorConstant.INVALID_DATA_ERROR_MESSAGE;
import static platform.core.common.service.persistence.constant.ErrorConstant.INVALID_EVENT_MESSAGE;
import static platform.core.common.service.persistence.constant.ErrorConstant.INVALID_INPUT_ERROR;

@Component
@Lazy(false)
@RequiredArgsConstructor
public class TripForSaleConsumer {


    @Value("${spring.kafka.events.trip-ready-for-sale}")
    private String tripReadyForSale;

    @Value("${spring.kafka.topics.notifications}")
    private String notificationTopic;

    @Value("${spring.kafka.events.notification-activities}")
    private String notificationActivitiesEvent;

    private final OutBoxService outBoxService;
    private final TripEventHandler tripEventHandler;
    private final SystemLog sLog = SystemLog.getLogger(this.getClass());

    @KafkaListener(
            topics = "${spring.kafka.topics.trips}",
            containerFactory = "kafkaListenerContainerFactory",
            groupId = "${spring.kafka.group-id.booking-trips}")
    public void consume(String payload, Acknowledgment acknowledgment) {
        sLog.info("[TRIP-FOR-SALE] Raw Payload: {}",  payload);

        DomainEvent event =
                JsonUtils.parseToKafkaObject(
                        payload,
                        new TypeReference<>() {
                        });

        sLog.info("[TRIP-FOR-SALE] Domain Event: {}", event);
        if (event == null
                || event.header() == null
                || event.payload() == null
                || event.header().get("context") == null
                || event.payload().get("data") == null) {
            throw new BusinessException(ExceptionUtils.buildResultResponse(INVALID_INPUT_ERROR, INVALID_DATA_ERROR_MESSAGE));
        }

        if (!tripReadyForSale.equals(event.eventType())) {
            sLog.info("Ignore event {}", event.eventType());
            acknowledgment.acknowledge();
            return;
        }

        BaseRequest context = JsonUtils.convertValue(event.header().get("context"), BaseRequest.class);
        TripSellableEvent tripEvent = JsonUtils.convertValue(event.payload().get("data"), TripSellableEvent.class);

        sLog.info("[TRIP-EVENT] Processing event: eventName={} eventId={} aggregateId={} tripId={} vehicleId={}",
                event.eventType(),
                event.eventId(),
                event.aggregateId(),
                tripEvent.tripId(),
                tripEvent.vehicleId());

        sLog.info("[TRIP-EVENT] Trip Sellable Event: {}", tripEvent);

        try {
            validateEvent(event, context, tripEvent);
            sLog.info("VALIDATED");
            tripEventHandler.generateRouteSeat(event, context, tripEvent);
        } catch (Exception ex) {
            sLog.error("[ROUTE-EVENT] Failed eventName={} eventId={} aggregateId={} routeId={} vehicleId={}",
                    event.eventType(),
                    event.eventId(),
                    event.aggregateId(),
                    tripEvent.tripId(),
                    tripEvent.vehicleId(),
                    ex);
            acknowledgment.acknowledge();
            return;
        }

        sLog.info("[ROUTE-EVENT] Event processed successfully: eventName={} eventId={} routeId={}", event.eventType(), event.eventId(), event.aggregateId());

        TripOpenForBookingEvent bookingEvent = TripOpenForBookingEvent
                .builder()
                .tripId(tripEvent.tripId())
                .vehicleId(tripEvent.vehicleId())
                .seatCount(tripEvent.seatCount())
                .creator(tripEvent.creator())
                .assignedAt(tripEvent.assignedAt())
                .build();

        outBoxService.generateEvent(
                tripEvent.tripId(),
                notificationTopic,
                notificationActivitiesEvent,
                notificationActivitiesEvent,
                bookingEvent,
                context
        );

        acknowledgment.acknowledge();
    }



    public void validateEvent(DomainEvent event, BaseRequest context, TripSellableEvent data) {
        if (event.eventId().isBlank()
                || event.eventType().isBlank()
                || event.aggregateId().isBlank()
                || context == null
                || context.getRequestId().isBlank()
                || context.getRequestDateTime().isBlank()
                || context.getChannel().isBlank()
                || data.tripId().isBlank()
                || data.vehicleId().isBlank()) {
            throw new BusinessException(ExceptionUtils.buildResultResponse(INVALID_INPUT_ERROR, String.format(INVALID_EVENT_MESSAGE, event.eventType())));
        }
    }

}
