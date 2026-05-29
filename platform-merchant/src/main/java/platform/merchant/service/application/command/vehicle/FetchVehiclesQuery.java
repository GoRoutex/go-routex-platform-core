package platform.merchant.service.application.command.vehicle;

import lombok.Builder;
import platform.core.common.service.common.RequestContext;
import platform.core.common.service.domain.vehicle.VehicleStatus;

@Builder
public record FetchVehiclesQuery(
        String pageSize,
        String pageNumber,
        String merchantId,
        VehicleStatus status,
        RequestContext context
) {
}
