package platform.management.service.interfaces.models.activity;

import lombok.Builder;

import java.time.OffsetDateTime;
import java.util.Map;

@Builder
public record RecentActivityItem(
        String id,
        String eventType,
        String aggregateId,
        String eventKey,
        OffsetDateTime occurredAt,
        String title,
        String message,
        String actorUserId,
        String actorName,
        String entityType,
        String entityId,
        String merchantId,
        Map<String, Object> header,
        Map<String, Object> payload
) {
}
