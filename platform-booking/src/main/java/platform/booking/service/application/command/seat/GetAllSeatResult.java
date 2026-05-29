package platform.booking.service.application.command.seat;

import lombok.Builder;

import java.util.List;

@Builder
public record GetAllSeatResult(
        List<SeatItemResult> seats
) {
}
