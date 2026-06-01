package platform.merchant.service.application.command.maintenance;

import lombok.Builder;
import platform.merchant.service.domain.maintenance.MaintenancePlanStatus;
import platform.merchant.service.domain.maintenance.MaintenancePlanType;
import platform.core.common.service.domain.vehicle.VehicleStatus;
import platform.core.common.service.domain.vehicle.VehicleTemplateCategory;
import platform.merchant.service.domain.vehicle.VehicleTemplateType;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
public record FetchMaintenancePlanDetailResult(
        String id,
        String merchantId,
        MaintenancePlanVehicleResult vehicle,
        String code,
        String title,
        String description,
        MaintenancePlanType type,
        MaintenancePlanStatus status,
        LocalDate plannedDate,
        LocalDate dueDate,
        LocalDate completedDate,
        Long currentOdometerKm,
        Long targetOdometerKm,
        BigDecimal estimatedCost,
        BigDecimal actualCost,
        String serviceProvider,
        String note
) {
    @Builder
    public record MaintenancePlanVehicleResult(
            String id,
            String templateId,
            VehicleStatus status,
            VehicleTemplateCategory category,
            VehicleTemplateType type,
            String vehiclePlate,
            Long seatCapacity,
            Boolean hasFloor,
            String manufacturer
    ) {
    }
}
