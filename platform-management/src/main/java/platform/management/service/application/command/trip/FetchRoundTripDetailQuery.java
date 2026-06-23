package platform.management.service.application.command.trip;

import lombok.Builder;
import platform.core.common.service.common.RequestContext;

@Builder
public record FetchRoundTripDetailQuery(
        String outboundTripId,
        String returnTripId,
        RequestContext context
) {
}
