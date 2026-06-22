package platform.management.service.application.command.trip;

import lombok.Builder;

@Builder
public record FetchRoundTripDetailResult(
        FetchTripResult outboundTrip,
        FetchTripResult returnTrip
) {
}
