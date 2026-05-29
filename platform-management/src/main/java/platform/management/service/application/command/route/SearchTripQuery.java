package platform.management.service.application.command.route;

import lombok.Builder;
import platform.core.common.service.application.command.common.PageContext;
import platform.core.common.service.common.RequestContext;

@Builder
public record SearchTripQuery(
        RequestContext context,
        String originName,
        String destinationName,
        String departureDate,
        String seat,
        PageContext pageContext
) {
}
