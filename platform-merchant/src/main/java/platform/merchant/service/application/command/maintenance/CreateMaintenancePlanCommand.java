package platform.merchant.service.application.command.maintenance;

import lombok.Builder;
import platform.core.common.service.common.RequestContext;
import platform.merchant.service.domain.maintenance.MaintenancePlanType;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
public record CreateMaintenancePlanCommand(
        RequestContext context,
        String merchantId,
        String creator,
        String vehicleId,
        String code,
        String title,
        String description,
        MaintenancePlanType type,
        LocalDate plannedDate,
        LocalDate dueDate,
        Long currentOdometerKm,
        Long targetOdometerKm,
        BigDecimal estimatedCost,
        String serviceProvider,
        String note
) {
}
