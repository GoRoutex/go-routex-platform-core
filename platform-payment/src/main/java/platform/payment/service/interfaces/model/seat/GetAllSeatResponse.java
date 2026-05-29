package platform.payment.service.interfaces.model.seat;


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
public class GetAllSeatResponse extends BaseResponse<List<GetAllSeatResponse.GetAvailableSeatResponseData>> {
    @Getter
    @Setter
    @NoArgsConstructor
    @SuperBuilder
    public static class GetAvailableSeatResponseData {
        private String routeId;
        private String seatNo;
        private String status;
        private String ticketId;
    }
}
