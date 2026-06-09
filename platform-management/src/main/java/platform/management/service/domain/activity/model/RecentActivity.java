package platform.management.service.domain.activity.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import platform.core.common.service.persistence.AbstractAuditingEntity;

import java.time.OffsetDateTime;
import java.util.Map;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class RecentActivity extends AbstractAuditingEntity {
    private String id;
    private String eventType;
    private String aggregateId;
    private String eventKey;
    private OffsetDateTime occurredAt;
    private String title;
    private String message;
    private String actorUserId;
    private String actorName;
    private String entityType;
    private String entityId;
    private String merchantId;
    private String audienceType;
    private String scopeType;
    private String scopeId;
    private String visibility;
    private String severity;
    private String status;
    private String sourceService;
    private String correlationId;
    private String entityDisplayName;
    private Map<String, Object> header;
    private Map<String, Object> payload;
}
