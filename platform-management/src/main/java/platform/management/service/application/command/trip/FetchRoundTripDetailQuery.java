package platform.management.service.application.command.trip;

import lombok.Builder;

@Builder
public record FetchRoundTripDetailQuery(
        String outboundTripId,
        String returnTripId,
        String requestId,
        String requestDateTime,
        String channel
) {
}
