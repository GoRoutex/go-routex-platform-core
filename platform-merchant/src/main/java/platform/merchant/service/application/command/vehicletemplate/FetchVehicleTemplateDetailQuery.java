package platform.merchant.service.application.command.vehicletemplate;

import lombok.Builder;
import platform.core.common.service.common.RequestContext;

@Builder
public record FetchVehicleTemplateDetailQuery(
        RequestContext context,
        String merchantId,
        String templateId
) {
}
