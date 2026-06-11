package platform.management.service.application.command.trip;

import lombok.Builder;
import platform.core.common.service.application.command.common.PageContext;
import platform.core.common.service.common.RequestContext;

@Builder
public record SearchRoundTripQuery(
        RequestContext context,
        SearchRoundTripDetailQuery outBoundTrip,
        SearchRoundTripDetailQuery returnTrip,
        PageContext pageContext,
        String seat
) {
}
