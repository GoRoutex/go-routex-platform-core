package platform.merchant.service.application.command.maintenance;

import lombok.Builder;
import platform.core.common.service.common.RequestContext;

@Builder
public record DeleteMaintenancePlanCommand(
        RequestContext context,
        String merchantId,
        String creator,
        String maintenancePlanId
) {
}
