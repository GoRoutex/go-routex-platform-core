package platform.merchant.service.interfaces.model.trip;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import platform.core.common.service.api.BaseResponse;
import platform.core.common.service.domain.trip.TripStatus;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class DeleteTripResponse extends BaseResponse<DeleteTripResponse.DeleteTripResponseData> {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    public static class DeleteTripResponseData {
        private String tripId;
        private TripStatus status;
    }
}
