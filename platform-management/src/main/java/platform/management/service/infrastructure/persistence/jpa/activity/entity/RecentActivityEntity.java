package platform.management.service.infrastructure.persistence.jpa.activity.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import platform.core.common.service.persistence.AbstractAuditingEntity;

import java.time.OffsetDateTime;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Entity
@Table(name = "RECENT_ACTIVITY")
public class RecentActivityEntity extends AbstractAuditingEntity {

    @Id
    private String id; // eventId

    @Column(name = "EVENT_TYPE", nullable = false)
    private String eventType;

    @Column(name = "AGGREGATE_ID")
    private String aggregateId;

    @Column(name = "EVENT_KEY")
    private String eventKey;

    @Column(name = "OCCURRED_AT", nullable = false)
    private OffsetDateTime occurredAt;

    @Column(name = "TITLE")
    private String title;

    @Column(name = "MESSAGE", columnDefinition = "text")
    private String message;

    @Column(name = "ACTOR_USER_ID")
    private String actorUserId;

    @Column(name = "ACTOR_NAME")
    private String actorName;

    @Column(name = "ENTITY_TYPE")
    private String entityType;

    @Column(name = "ENTITY_ID")
    private String entityId;

    @Column(name = "MERCHANT_ID")
    private String merchantId;

    @Column(name = "AUDIENCE_TYPE")
    private String audienceType;

    @Column(name = "SCOPE_TYPE")
    private String scopeType;

    @Column(name = "SCOPE_ID")
    private String scopeId;

    @Column(name = "VISIBILITY")
    private String visibility;

    @Column(name = "SEVERITY")
    private String severity;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "SOURCE_SERVICE")
    private String sourceService;

    @Column(name = "CORRELATION_ID")
    private String correlationId;

    @Column(name = "ENTITY_DISPLAY_NAME")
    private String entityDisplayName;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "HEADER", columnDefinition = "jsonb")
    private Map<String, Object> header;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "PAYLOAD", columnDefinition = "jsonb")
    private Map<String, Object> payload;
}
