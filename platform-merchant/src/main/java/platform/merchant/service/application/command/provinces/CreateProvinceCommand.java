package platform.merchant.service.application.command.provinces;

import lombok.Builder;
import platform.core.common.service.common.RequestContext;

@Builder
public record CreateProvinceCommand(
        RequestContext context,
        String name,
        String code
) {
}

