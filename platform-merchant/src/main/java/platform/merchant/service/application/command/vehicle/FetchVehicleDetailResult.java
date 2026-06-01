package platform.merchant.service.application.command.vehicle;

import lombok.Builder;
import platform.core.common.service.domain.vehicle.VehicleStatus;
import platform.core.common.service.domain.vehicle.VehicleTemplateCategory;
import platform.merchant.service.domain.vehicle.VehicleTemplateType;

@Builder
public record FetchVehicleDetailResult(
        String id,
        String merchantId,
        String templateId,
        String creator,
        VehicleStatus status,
        VehicleTemplateCategory category,
        VehicleTemplateType type,
        String vehiclePlate,
        Long seatCapacity,
        Boolean hasFloor,
        String manufacturer
) {
}
