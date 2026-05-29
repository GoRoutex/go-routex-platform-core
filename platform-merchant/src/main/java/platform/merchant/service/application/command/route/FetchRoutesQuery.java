package platform.merchant.service.application.command.route;

import lombok.Builder;
import platform.core.common.service.common.RequestContext;
import platform.merchant.service.domain.route.RouteStatus;

@Builder
public record FetchRoutesQuery(
        RequestContext context,
        RouteStatus status,
        String pageSize,
        String pageNumber,
        String merchantId,
        String merchantName
) {
}
