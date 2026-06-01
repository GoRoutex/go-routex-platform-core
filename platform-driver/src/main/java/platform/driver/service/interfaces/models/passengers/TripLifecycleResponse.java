package platform.driver.service.interfaces.models.passengers;

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
public class TripLifecycleResponse extends BaseResponse<TripLifecycleResponse.TripLifecycleResponseData> {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    public static class TripLifecycleResponseData {
        private String tripId;
        private TripStatus tripStatus;
        private int totalTickets;
        private int boardedTickets;
        private int completedTickets;
        private int expiredTickets;
        private int unchangedTickets;
    }
}
