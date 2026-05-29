package platform.merchant.service.interfaces.model.trip;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import platform.core.common.service.api.BaseResponse;
import platform.core.common.service.domain.trip.TripStatus;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class FetchTripDetailResponse extends BaseResponse<FetchTripDetailResponse.FetchTripDetailResponseData> {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    public static class FetchTripRouteData {
        private String routeId;
        private String originName;
        private String originCode;
        private String destinationName;
        private String destinationCode;
        private String originDepartmentId;
        private String destinationDepartmentId;
        private Long duration;
    }
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    public static class FetchTripDetailResponseData {
        private String tripId;
        private String tripCode;
        private String creator;
        private OffsetDateTime departureTime;
        private String rawDepartureTime;
        private String rawDepartureDate;
        private String rawArrivalTime;
        private TripStatus status;
        private FetchTripRouteData route;
    }
}
