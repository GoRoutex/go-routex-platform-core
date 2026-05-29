package platform.merchant.service.application.command.driver;

import lombok.Builder;
import platform.core.common.service.common.RequestContext;

@Builder
public record DeleteDriverCommand(
        RequestContext context,
        String merchantId,
        String creator,
        String driverId
) {
}
