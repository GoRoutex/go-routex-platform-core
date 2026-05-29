package platform.payment.service.interfaces.model.seat;

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

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    @SuperBuilder
    public static class HoldSeatResponseBookingInfo {
        private String bookingId;
        private String bookingCode;
        private String holdToken;
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
        private String routeId;
        private String seatNo;
        private String status;
        private OffsetDateTime holdUntil;
        private String holdToken;
        private String holdBy;
        private HoldSeatResponseBookingInfo booking;
    }
}
