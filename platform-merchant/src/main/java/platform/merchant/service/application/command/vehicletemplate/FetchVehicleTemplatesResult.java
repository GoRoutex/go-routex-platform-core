package platform.merchant.service.application.command.vehicletemplate;

import lombok.Builder;
import platform.core.common.service.domain.vehicle.FuelType;
import platform.core.common.service.domain.vehicle.VehicleTemplateCategory;
import platform.core.common.service.domain.vehicle.VehicleTemplateStatus;
import platform.merchant.service.domain.vehicle.VehicleTemplateType;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record FetchVehicleTemplatesResult(
        List<FetchVehicleTemplateItemResult> items,
        int pageNumber,
        int pageSize,
        long totalElements,
        int totalPages
) {
    @Builder
    public record FetchVehicleTemplateItemResult(
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
}
