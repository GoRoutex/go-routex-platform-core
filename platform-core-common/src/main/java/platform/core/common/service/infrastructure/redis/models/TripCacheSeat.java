package platform.core.common.service.infrastructure.redis.models;

import lombok.Builder;
import platform.core.common.service.domain.seat.SeatStatus;
import platform.core.common.service.domain.seat.SeatFloor;


import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class TripCacheSeat {
    private String tripId;
    private String seatId;
    private String seatNo;
    private String seatTemplateId;
    private SeatStatus status;
    private SeatFloor floor;
    private int rowNo;
    private int colNo;
}
