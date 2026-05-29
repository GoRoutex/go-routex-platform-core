package platform.booking.service.application.command.seat;

import lombok.Builder;
import platform.core.common.service.domain.seat.SeatStatus;

@Builder
public record SeatItemResult(
        String routeId,
        String seatNo,
        SeatStatus status
) {
}
