package platform.management.service.application.command.route;

import lombok.Builder;

@Builder
public record FetchTripQuery(
        String tripId,
        String requestId,
        String requestDateTime,
        String channel
) {
}
