package platform.core.common.service.infrastructure.kafka.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import platform.core.common.service.api.BaseRequest;
import platform.core.common.service.domain.outbox.model.OutBoxEvent;
import platform.core.common.service.infrastructure.event.DomainEvent;
import platform.core.common.service.infrastructure.kafka.model.KafkaEventMessage;
import platform.core.common.service.persistence.utils.JsonUtils;
import vn.com.go.routex.identity.security.log.SystemLog;

import java.time.OffsetDateTime;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
public class KafkaEventPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final SystemLog sLog = SystemLog.getLogger(this.getClass());

    public CompletableFuture<Void> publishAsync(OutBoxEvent outboxEvent) {

        String jsonPayload;
        try {
            DomainEvent domainEvent = toDomainEvent(outboxEvent);
            jsonPayload = objectMapper.writeValueAsString(domainEvent);
            sLog.info("[DomainEvent] {}", jsonPayload);
        } catch (Exception e) {
            sLog.error("[OUTBOX-SERIALIZE-ERROR] eventId={}", outboxEvent.getId(), e);
            return CompletableFuture.failedFuture(e);
        }
        CompletableFuture<SendResult<String, String>> kafkaFuture = kafkaTemplate.send(outboxEvent.getTopic(), outboxEvent.getAggregateId(), jsonPayload);
        return kafkaFuture.thenAccept(result -> {
            RecordMetadata recordMetadata = result.getRecordMetadata();
            sLog.info("[OUTBOX-PUBLISH-SUCCESS] eventId={}, topic={}, partition={}, offset={}",
                    outboxEvent.getId(),
                    outboxEvent.getTopic(),
                    recordMetadata.partition(),
                    recordMetadata.offset());
        }).exceptionally(ex -> {
            sLog.error(
                    "[OUTBOX-PUBLISH-FAILED] eventId={}, topic={}",
                    outboxEvent.getId(),
                    outboxEvent.getTopic(),
                    ex
            );
            throw new RuntimeException(ex);
        });
    }

    public void publish(
            BaseRequest request,
            String topicName,
            String eventName,
            String aggregateId,
            Object payload
    ) {
        try {

            KafkaEventMessage<Object> message =
                    KafkaEventMessage.builder()
                            .eventId(UUID.randomUUID().toString())
                            .eventType(eventName)
                            .aggregateId(aggregateId)
                            .eventKey(eventName)
                            .header(KafkaEventMessage.MessageHeader.builder()
                                    .context(KafkaEventMessage.MessageContext.builder()
                                            .requestId(request.getRequestId())
                                            .requestDateTime(request.getRequestDateTime())
                                            .channel(request.getChannel())
                                            .build())
                                    .build())
                            .payload(KafkaEventMessage.MessagePayload.builder()
                                    .data(payload)
                                    .build())
                            .source("booking-service")
                            .version(1)
                            .occurredAt(OffsetDateTime.now())
                            .build();

            String json = JsonUtils.parseToJsonStr(message);

            kafkaTemplate.send(topicName, aggregateId, json);
        } catch(Exception ex) {
            throw new IllegalArgumentException("Kafka publish failed: ", ex);
        }
    }

    public void publish(
            String requestId,
            String requestDateTime,
            String channel,
            String topicName,
            String eventName,
            String aggregateId,
            Object payload
    ) {
        try {
            KafkaEventMessage<Object> message =
                    KafkaEventMessage.builder()
                            .eventId(UUID.randomUUID().toString())
                            .eventType(eventName)
                            .aggregateId(aggregateId)
                            .eventKey(eventName)
                            .header(KafkaEventMessage.MessageHeader.builder()
                                    .context(KafkaEventMessage.MessageContext.builder()
                                            .requestId(requestId)
                                            .requestDateTime(requestDateTime)
                                            .channel(channel)
                                            .build())
                                    .build())
                            .payload(KafkaEventMessage.MessagePayload.builder()
                                    .data(payload)
                                    .build())
                            .source("booking-service")
                            .version(1)
                            .occurredAt(OffsetDateTime.now())
                            .build();

            String json = JsonUtils.parseToJsonStr(message);
            kafkaTemplate.send(topicName, aggregateId, json);
        } catch (Exception ex) {
            throw new IllegalArgumentException("Kafka publish failed: ", ex);
        }
    }

    private DomainEvent toDomainEvent(OutBoxEvent outBoxEvent) {

        if(outBoxEvent == null) {
            return null;
        }
        return DomainEvent.builder()
                .eventId(outBoxEvent.getId())
                .eventType(outBoxEvent.getEventType())
                .eventKey(outBoxEvent.getEventKey())
                .aggregateId(outBoxEvent.getAggregateId())
                .header(outBoxEvent.getHeader())
                .payload(outBoxEvent.getPayload())
                .occurredAt(outBoxEvent.getProcessedAt())
                .build();
    }
}
