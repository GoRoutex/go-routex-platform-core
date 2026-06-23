package platform.management.service.interfaces.models.seat;

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
public class SearchRoundTripSeatResponse extends BaseResponse<SearchRoundTripSeatResponse.SearchRoundTripSeatResponseData> {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    public static class SearchRoundTripSeatResponseData {
        private SearchRoundTripSeatResponsePage outboundSeats;
        private SearchRoundTripSeatResponsePage returnSeats;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    public static class SearchRoundTripSeatResponsePage {
        private String tripId;
        private List<SearchSeatResponse.SearchSeatResponseData> items;
    }
}
