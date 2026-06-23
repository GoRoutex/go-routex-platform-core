package platform.booking.service.application.command.seat;

import lombok.Builder;

@Builder
public record HoldRoundTripSeatResult(
        HoldSeatResult outboundTrip,
        HoldSeatResult returnTrip
) {
}
