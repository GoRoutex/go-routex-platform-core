package platform.merchant.service.application.command.maintenance;

import lombok.Builder;
import platform.core.common.service.common.RequestContext;
import platform.merchant.service.domain.maintenance.MaintenancePlanStatus;
import platform.merchant.service.domain.maintenance.MaintenancePlanType;

import java.time.LocalDate;

@Builder
public record FetchMaintenancePlansQuery(
        String pageSize,
        String pageNumber,
        String merchantId,
        String vehicleId,
        MaintenancePlanStatus status,
        MaintenancePlanType type,
        LocalDate fromPlannedDate,
        LocalDate toPlannedDate,
        RequestContext context
) {
}
