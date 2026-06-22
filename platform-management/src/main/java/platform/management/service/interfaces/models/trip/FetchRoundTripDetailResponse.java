package platform.management.service.interfaces.models.trip;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import platform.core.common.service.api.BaseResponse;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class FetchRoundTripDetailResponse extends BaseResponse<FetchRoundTripDetailResponse.FetchRoundTripDetailResponseData> {

    @Getter
    @Setter
    @NoArgsConstructor
    @SuperBuilder
    public static class FetchRoundTripDetailResponseData {
        private FetchTripResponse.FetchTripResponseData outboundTrip;
        private FetchTripResponse.FetchTripResponseData returnTrip;
    }
}
