package platform.merchant.service.application.command.route;

import lombok.Builder;

import java.util.List;

@Builder
public record AssignRouteBatchResult(
        int successCount,
        int failedCount,
        List<AssignRouteResult> successItems,
        List<AssignRouteBatchFailedItem> failedItems
) {
}
