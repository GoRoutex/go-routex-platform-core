package platform.merchant.service.application.command.driver;

import lombok.Builder;
import platform.core.common.service.common.RequestContext;

@Builder
public record FetchDriverDetailQuery(
        RequestContext context,
        String merchantId,
        String driverId,
        String userId,
        String employeeCode
) {
}
