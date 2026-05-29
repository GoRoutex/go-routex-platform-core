package platform.merchant.service.application.command.vehicle;

import lombok.Builder;
import platform.core.common.service.common.RequestContext;

@Builder
public record FetchVehicleDetailQuery(
        RequestContext context,
        String merchantId,
        String vehicleId
) {
}
