package platform.management.service.application.command.seat;

import lombok.Builder;
import platform.core.common.service.domain.seat.SeatFloor;
import platform.core.common.service.domain.seat.SeatStatus;

import java.util.List;

@Builder
public record SearchSeatResult(
        List<SearchSeatResultData> data
) {

    @Builder
    public record SearchSeatResultData(
            String seatId,
            SeatFloor floor,
            int colNo,
            int rowNo,
            SeatStatus status,
            String code
    ) {}
}
