package platform.merchant.service.application.command.vehicletemplate;

import lombok.Builder;
import platform.core.common.service.domain.vehicle.VehicleTemplateStatus;

@Builder
public record DeleteVehicleTemplateResult(
        String id,
        String code,
        VehicleTemplateStatus status
) {
}
