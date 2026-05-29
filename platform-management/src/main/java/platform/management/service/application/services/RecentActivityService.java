package platform.management.service.application.services;

import org.springframework.data.domain.Page;
import platform.management.service.domain.activity.model.RecentActivity;

import java.time.OffsetDateTime;
import java.util.Set;

public interface RecentActivityService {

    void record(DomainEvent event);

    Page<RecentActivity> fetch(
            OffsetDateTime from,
            OffsetDateTime to,
            String merchantId,
            Set<String> eventTypes,
            int pageNumber,
            int pageSize
    );
}
