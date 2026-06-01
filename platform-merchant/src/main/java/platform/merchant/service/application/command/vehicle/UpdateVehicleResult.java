package platform.merchant.service.application.command.vehicle;

import lombok.Builder;
import platform.core.common.service.domain.vehicle.VehicleStatus;
import platform.core.common.service.domain.vehicle.VehicleTemplateCategory;
import platform.merchant.service.domain.vehicle.VehicleTemplateType;

@Builder
public record UpdateVehicleResult(
        String id,
        String templateId,
        String creator,
        VehicleTemplateCategory category,
        VehicleTemplateType type,
        String vehiclePlate,
        Long seatCapacity,
        Boolean hasFloor,
        String manufacturer,
        VehicleStatus status
) {
}
