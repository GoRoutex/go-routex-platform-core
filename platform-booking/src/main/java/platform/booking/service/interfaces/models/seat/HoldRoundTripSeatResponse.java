package platform.booking.service.interfaces.models.seat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import platform.core.common.service.api.BaseResponse;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class HoldRoundTripSeatResponse extends BaseResponse<HoldRoundTripSeatResponse.HoldRoundTripSeatResponseData> {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    public static class HoldRoundTripSeatResponseData {
        private HoldRoundTripSeatLegResponse outboundTrip;
        private HoldRoundTripSeatLegResponse returnTrip;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    public static class HoldRoundTripSeatLegResponse {
        private HoldSeatResponse.HoldSeatResponseBookingInfo booking;
        private List<HoldSeatResponse.HoldSeatResponseData> seats;
    }
}
