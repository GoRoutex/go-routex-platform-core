package platform.merchant.service.application.command.maintenance;

import lombok.Builder;
import platform.merchant.service.domain.maintenance.MaintenancePlanStatus;

@Builder
public record DeleteMaintenancePlanResult(
        String id,
        String code,
        MaintenancePlanStatus status
) {
}
