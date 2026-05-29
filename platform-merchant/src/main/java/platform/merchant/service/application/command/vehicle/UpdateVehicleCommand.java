package platform.merchant.service.application.command.vehicle;

import lombok.Builder;
import platform.core.common.service.common.RequestContext;
import platform.core.common.service.domain.vehicle.VehicleStatus;

@Builder
public record UpdateVehicleCommand(
        RequestContext context,
        String merchantId,
        String creator,
        String vehicleId,
        String templateId,
        String vehiclePlate,
        VehicleStatus status
) {
}
