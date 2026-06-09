package platform.management.service.application.services;

import org.springframework.data.domain.Page;
import platform.core.common.service.infrastructure.event.DomainEvent;
import platform.management.service.domain.activity.model.RecentActivity;

import java.time.OffsetDateTime;
import java.util.Set;

public interface RecentActivityService {

    void record(DomainEvent event);

    Page<RecentActivity> fetch(
            OffsetDateTime from,
            OffsetDateTime to,
            String audienceType,
            String scopeType,
            String scopeId,
            String merchantId,
            Set<String> eventTypes,
            String severity,
            String status,
            String sourceService,
            String entityType,
            String entityId,
            String actorUserId,
            String keyword,
            int pageNumber,
            int pageSize
    );
}
