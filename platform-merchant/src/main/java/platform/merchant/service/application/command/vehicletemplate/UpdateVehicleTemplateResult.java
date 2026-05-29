package platform.merchant.service.application.command.vehicletemplate;

import lombok.Builder;
import platform.core.common.service.domain.vehicle.FuelType;
import platform.core.common.service.domain.vehicle.VehicleTemplateCategory;
import platform.core.common.service.domain.vehicle.VehicleTemplateStatus;
import platform.core.common.service.domain.vehicle.VehicleTemplateType;

import java.math.BigDecimal;

@Builder
public record UpdateVehicleTemplateResult(
        String id,
        String merchantId,
        String code,
        String name,
        String manufacturer,
        String model,
        Long seatCapacity,
        VehicleTemplateCategory category,
        VehicleTemplateType type,
        FuelType fuelType,
        Boolean hasFloor,
        BigDecimal ticketPrice,
        VehicleTemplateStatus status
) {
}
