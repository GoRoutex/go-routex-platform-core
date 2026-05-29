package platform.merchant.service.application.command.route;

import lombok.Builder;
import platform.core.common.service.common.RequestContext;

@Builder
public record AssignRouteCommand(
        String merchantId,
        String creator,
        String tripId,
        String vehicleId,
        String driverId,
        RequestContext context
) {
}
