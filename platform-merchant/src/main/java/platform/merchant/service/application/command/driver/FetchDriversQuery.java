package platform.merchant.service.application.command.driver;

import lombok.Builder;
import platform.core.common.service.common.RequestContext;

@Builder
public record FetchDriversQuery(
        RequestContext context,
        String merchantId,
        String status,
        String pageNumber,
        String pageSize
) {
}
