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
import java.util.HashMap;
import java.util.Map;
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

    public void publishRecentActivity(
            String topicName,
            String audienceType,
            String scopeType,
            String scopeId,
            String visibility,
            String severity,
            String status,
            String sourceService,
            String correlationId,
            String merchantId,
            String eventKey,
            String aggregateId,
            String title,
            String message,
            String actorUserId,
            String actorName,
            String entityType,
            String entityId,
            String entityDisplayName,
            Map<String, Object> metadata
    ) {
        try {
            Map<String, Object> header = new HashMap<>();
            putIfNotBlank(header, "audienceType", audienceType);
            putIfNotBlank(header, "scopeType", scopeType);
            putIfNotBlank(header, "scopeId", scopeId);
            putIfNotBlank(header, "visibility", visibility);
            putIfNotBlank(header, "severity", severity);
            putIfNotBlank(header, "status", status);
            putIfNotBlank(header, "sourceService", sourceService);
            putIfNotBlank(header, "correlationId", correlationId);
            putIfNotBlank(header, "merchantId", merchantId);

            Map<String, Object> data = new HashMap<>();
            putIfNotBlank(data, "audienceType", audienceType);
            putIfNotBlank(data, "scopeType", scopeType);
            putIfNotBlank(data, "scopeId", scopeId);
            putIfNotBlank(data, "visibility", visibility);
            putIfNotBlank(data, "severity", severity);
            putIfNotBlank(data, "status", status);
            putIfNotBlank(data, "sourceService", sourceService);
            putIfNotBlank(data, "correlationId", correlationId);
            putIfNotBlank(data, "merchantId", merchantId);
            putIfNotBlank(data, "title", title);
            putIfNotBlank(data, "message", message);
            putIfNotBlank(data, "actorUserId", actorUserId);
            putIfNotBlank(data, "actorName", actorName);
            putIfNotBlank(data, "entityType", entityType);
            putIfNotBlank(data, "entityId", entityId);
            putIfNotBlank(data, "entityDisplayName", entityDisplayName);
            if (metadata != null && !metadata.isEmpty()) {
                data.put("metadata", metadata);
            }

            DomainEvent event = DomainEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .eventType("routex.recent-activity." + normalizeEventKey(eventKey))
                    .eventKey(eventKey)
                    .aggregateId(aggregateId)
                    .occurredAt(OffsetDateTime.now())
                    .header(header)
                    .payload(Map.of("data", data))
                    .build();

            kafkaTemplate.send(topicName, aggregateId, objectMapper.writeValueAsString(event));
        } catch (Exception ex) {
            throw new IllegalArgumentException("Kafka recent activity publish failed: ", ex);
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

    private static void putIfNotBlank(Map<String, Object> target, String key, String value) {
        if (value != null && !value.isBlank()) {
            target.put(key, value);
        }
    }

    private static String normalizeEventKey(String eventKey) {
        if (eventKey == null || eventKey.isBlank()) {
            return "unknown";
        }
        return eventKey.trim().toLowerCase().replace('_', '-');
    }
}
