package platform.booking.service.interfaces.models.seat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import platform.core.common.service.api.BaseResponse;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class HoldSeatResponse extends BaseResponse<List<HoldSeatResponse.HoldSeatResponseData>> {

    private HoldSeatResponseBookingInfo booking;

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    @SuperBuilder
    public static class HoldSeatResponseBookingInfo {
        private String bookingId;
        private String bookingCode;
        private OffsetDateTime holdUntil;
        private Integer seatCount;
        private BigDecimal totalAmount;
        private String currency;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @SuperBuilder
    public static class HoldSeatResponseData {
        private String tripId;
        private String seatNo;
        private String status;
        private String holdToken;
    }
}
