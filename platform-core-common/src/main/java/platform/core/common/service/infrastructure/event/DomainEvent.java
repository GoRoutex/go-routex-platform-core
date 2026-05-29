package platform.core.common.service.infrastructure.event;

import lombok.Builder;

import java.time.OffsetDateTime;
import java.util.Map;

@Builder
public record DomainEvent(
        String eventId,
        String eventType,
        String aggregateId,
        String eventKey,
        OffsetDateTime occurredAt,
        Map<String, Object> header,
        Map<String, Object> payload
) {
}
