package platform.management.service.application.command.route;

import lombok.Builder;
import platform.core.common.service.application.command.common.PageContext;
import platform.core.common.service.common.RequestContext;

@Builder
public record FetchTripsQuery(
        RequestContext context,
        PageContext pageContext,
        String merchantId,
        String merchantName
) {
}
