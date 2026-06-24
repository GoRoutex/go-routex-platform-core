package platform.management.service.application.command.trip;

import lombok.Builder;
import platform.core.common.service.application.command.common.PageContext;
import platform.core.common.service.common.RequestContext;

@Builder
public record FetchAdminTripsQuery(
        RequestContext context,
        PageContext pageContext,
        String departureDate
) {
}
