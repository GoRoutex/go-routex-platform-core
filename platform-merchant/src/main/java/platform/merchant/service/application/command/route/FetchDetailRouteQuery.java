package platform.merchant.service.application.command.route;


import lombok.Builder;
import platform.core.common.service.common.RequestContext;

@Builder
public record FetchDetailRouteQuery(
        RequestContext context,
        String routeId,
        String merchantId
) {
}
