package platform.management.service.application.command.trip;

import lombok.Builder;

@Builder
public record FetchTripQuery(
        String tripId,
        String requestId,
        String requestDateTime,
        String channel
) {
}
