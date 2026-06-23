package platform.management.service.application.command.seat;

import lombok.Builder;

@Builder
public record SearchRoundTripSeatResult(
        SearchSeatResult outboundSeats,
        SearchSeatResult returnSeats
) {
}
