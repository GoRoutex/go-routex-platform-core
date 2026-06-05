package platform.management.service.interfaces.models.seat;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import platform.core.common.service.api.BaseResponse;
import platform.core.common.service.domain.seat.SeatFloor;
import platform.core.common.service.domain.seat.SeatStatus;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class SearchSeatResponse extends BaseResponse<SearchSeatResponse.SearchSeatResponsePage> {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    public static class SearchSeatResponsePage {
        private List<SearchSeatResponseData> items;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    public static class SearchSeatResponseData {
        private String seatId;
        private String code;
        private SeatStatus status;
        private SeatFloor floor;
        private int rowNo;
        private int colNo;
    }
}
