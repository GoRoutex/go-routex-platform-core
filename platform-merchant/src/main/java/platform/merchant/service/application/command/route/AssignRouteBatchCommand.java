package platform.merchant.service.application.command.route;

import lombok.Builder;
import platform.core.common.service.common.RequestContext;

import java.util.List;

@Builder
public record AssignRouteBatchCommand(
        String merchantId,
        String creator,
        RequestContext context,
        List<AssignRouteBatchItem> assignments
) {
    @Builder
    public record AssignRouteBatchItem(
            String tripId,
            String vehicleId,
            String driverId
    ) {}
}
