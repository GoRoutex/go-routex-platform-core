package platform.merchant.service.application.command.vehicletemplate;

import lombok.Builder;
import platform.core.common.service.common.RequestContext;
import platform.core.common.service.domain.vehicle.VehicleTemplateCategory;
import platform.core.common.service.domain.vehicle.VehicleTemplateStatus;
import platform.merchant.service.domain.vehicle.VehicleTemplateType;

@Builder
public record FetchVehicleTemplatesQuery(
        String pageSize,
        String pageNumber,
        String merchantId,
        VehicleTemplateStatus status,
        VehicleTemplateCategory category,
        VehicleTemplateType type,
        RequestContext context
) {
}
