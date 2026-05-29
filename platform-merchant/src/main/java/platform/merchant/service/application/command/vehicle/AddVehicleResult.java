package platform.merchant.service.application.command.vehicle;

import lombok.Builder;
import platform.core.common.service.domain.vehicle.VehicleStatus;
import platform.core.common.service.domain.vehicle.VehicleTemplateCategory;
import platform.core.common.service.domain.vehicle.VehicleTemplateType;

@Builder
public record AddVehicleResult(
        String id,
        String templateId,
        String creator,
        VehicleTemplateType type,
        VehicleTemplateCategory category,
        String vehiclePlate,
        Long seatCapacity,
        String manufacturer,
        VehicleStatus status
) {
}
