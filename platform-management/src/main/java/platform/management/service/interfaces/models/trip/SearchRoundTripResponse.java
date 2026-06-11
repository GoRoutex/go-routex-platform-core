package platform.management.service.interfaces.models.trip;

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
public class SearchRoundTripResponse extends BaseResponse<SearchRoundTripResponse.SearchRoundTripResponseData> {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    public static class SearchRoundTripResponseData {
        private List<SearchTripResponse.SearchTripResponseData> outboundTrips;
        private List<SearchTripResponse.SearchTripResponseData> returnTrips;
    }

}
