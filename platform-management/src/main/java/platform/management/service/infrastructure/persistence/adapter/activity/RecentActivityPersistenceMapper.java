package platform.management.service.infrastructure.persistence.adapter.activity;


import org.springframework.stereotype.Component;
import platform.management.service.domain.activity.model.RecentActivity;
import platform.management.service.infrastructure.persistence.jpa.activity.entity.RecentActivityEntity;

@Component
public class RecentActivityPersistenceMapper {

    public RecentActivity toDomain(RecentActivityEntity entity) {
        if(entity == null) {
            return null;
        }

        return RecentActivity.builder()
                .id(entity.getId())
                .eventType(entity.getEventType())
                .aggregateId(entity.getAggregateId())
                .eventKey(entity.getEventKey())
                .occurredAt(entity.getOccurredAt())
                .title(entity.getTitle())
                .message(entity.getMessage())
                .actorUserId(entity.getActorUserId())
                .actorName(entity.getActorName())
                .entityType(entity.getEntityType())
                .entityId(entity.getEntityId())
                .merchantId(entity.getMerchantId())
                .audienceType(entity.getAudienceType())
                .scopeType(entity.getScopeType())
                .scopeId(entity.getScopeId())
                .visibility(entity.getVisibility())
                .severity(entity.getSeverity())
                .status(entity.getStatus())
                .sourceService(entity.getSourceService())
                .correlationId(entity.getCorrelationId())
                .entityDisplayName(entity.getEntityDisplayName())
                .header(entity.getHeader())
                .payload(entity.getPayload())
                .build();
    }

    public RecentActivityEntity toEntity(RecentActivity entity) {

        if(entity == null) {
            return null;
        }

        return RecentActivityEntity.builder()
                .id(entity.getId())
                .eventType(entity.getEventType())
                .aggregateId(entity.getAggregateId())
                .eventKey(entity.getEventKey())
                .occurredAt(entity.getOccurredAt())
                .title(entity.getTitle())
                .message(entity.getMessage())
                .actorUserId(entity.getActorUserId())
                .actorName(entity.getActorName())
                .entityType(entity.getEntityType())
                .entityId(entity.getEntityId())
                .merchantId(entity.getMerchantId())
                .audienceType(entity.getAudienceType())
                .scopeType(entity.getScopeType())
                .scopeId(entity.getScopeId())
                .visibility(entity.getVisibility())
                .severity(entity.getSeverity())
                .status(entity.getStatus())
                .sourceService(entity.getSourceService())
                .correlationId(entity.getCorrelationId())
                .entityDisplayName(entity.getEntityDisplayName())
                .header(entity.getHeader())
                .payload(entity.getPayload())
                .build();
    }
}
