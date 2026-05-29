package platform.merchant.service.application.command.vehicle;

import lombok.Builder;
import platform.core.common.service.common.RequestContext;

@Builder
public record AddVehicleCommand(
        RequestContext context,
        String merchantId,
        String creator,
        String templateId,
        boolean hasFloor,
        String vehiclePlate
) {
}
