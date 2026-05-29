package platform.merchant.service.application.command.maintenance;

import lombok.Builder;
import platform.core.common.service.common.RequestContext;
import platform.merchant.service.domain.maintenance.MaintenancePlanStatus;
import platform.merchant.service.domain.maintenance.MaintenancePlanType;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
public record UpdateMaintenancePlanCommand(
        RequestContext context,
        String merchantId,
        String creator,
        String maintenancePlanId,
        String vehicleId,
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
}
