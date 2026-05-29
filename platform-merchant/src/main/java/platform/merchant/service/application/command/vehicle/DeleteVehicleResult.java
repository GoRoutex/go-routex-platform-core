package platform.merchant.service.application.command.vehicle;

import lombok.Builder;
import platform.core.common.service.domain.vehicle.VehicleStatus;

@Builder
public record DeleteVehicleResult(
        String id,
        VehicleStatus status
) {
}

