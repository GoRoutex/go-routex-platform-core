package platform.merchant.service.application.command.route;

import lombok.Builder;
import platform.core.common.service.common.RequestContext;

@Builder
public record SearchRouteQuery(
        RequestContext context,
        String merchantId,
        String origin,
        String destination,
        String departureDate,
        String seat,
        String fromTime,
        String toTime,
        String pageSize,
        String pageNumber
) {
}
