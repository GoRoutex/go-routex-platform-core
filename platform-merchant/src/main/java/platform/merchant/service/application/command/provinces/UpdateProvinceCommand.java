package platform.merchant.service.application.command.provinces;

import lombok.Builder;
import platform.core.common.service.common.RequestContext;

@Builder
public record UpdateProvinceCommand(
        RequestContext context,
        String id,
        String name,
        String code
) {
}

