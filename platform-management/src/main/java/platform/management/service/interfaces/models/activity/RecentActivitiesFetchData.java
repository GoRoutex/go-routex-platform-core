package platform.management.service.interfaces.models.activity;

import lombok.Builder;

import java.util.List;

@Builder
public record RecentActivitiesFetchData(
        List<RecentActivityItem> items,
        int pageNumber,
        int pageSize,
        long totalElements,
        int totalPages,
        boolean hasNext
) {
}

