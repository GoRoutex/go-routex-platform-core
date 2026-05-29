package platform.core.common.service.persistence.jpa.outbox.adapter;

import org.springframework.stereotype.Component;
import platform.core.common.service.domain.outbox.model.OutBoxEvent;
import platform.core.common.service.persistence.jpa.outbox.entity.OutBoxEventEntity;

@Component
public class OutboxEventPersistenceMapper {

    public OutBoxEvent toDomain(OutBoxEventEntity entity) {
        if (entity == null) {
            return null;
        }

        return OutBoxEvent.builder()
                .id(entity.getId())
                .aggregateId(entity.getAggregateId())
                .topic(entity.getTopic())
                .eventType(entity.getEventType())
                .eventKey(entity.getEventKey())
                .payload(entity.getPayload())
                .header(entity.getHeader())
                .status(entity.getStatus())
                .retryCount(entity.getRetryCount())
                .availableAt(entity.getAvailableAt())
                .processedAt(entity.getProcessedAt())
                .createdAt(entity.getCreatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedAt(entity.getUpdatedAt())
                .updatedBy(entity.getUpdatedBy())
                .build();
    }

    public OutBoxEventEntity toEntity(OutBoxEvent domain) {
        if (domain == null) {
            return null;
        }

        return OutBoxEventEntity.builder()
                .id(domain.getId())
                .aggregateId(domain.getAggregateId())
                .topic(domain.getTopic())
                .eventType(domain.getEventType())
                .eventKey(domain.getEventKey())
                .payload(domain.getPayload())
                .header(domain.getHeader())
                .status(domain.getStatus())
                .retryCount(domain.getRetryCount())
                .availableAt(domain.getAvailableAt())
                .processedAt(domain.getProcessedAt())
                .createdAt(domain.getCreatedAt())
                .createdBy(domain.getCreatedBy())
                .updatedAt(domain.getUpdatedAt())
                .updatedBy(domain.getUpdatedBy())
                .build();
    }
}
