package platform.merchant.service.interfaces.model.trip;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import platform.core.common.service.api.BaseRequest;

import java.time.OffsetDateTime;
import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class CreateTripBatchRequest extends BaseRequest {

    @Valid
    @NotNull
    private CreateTripBatchRequestData data;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    public static class CreateTripBatchRequestData {
        private String routeId;
        private List<TripBatchData> trips;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    public static class TripBatchData {
        private OffsetDateTime departureTime;
        private String rawDepartureTime;
        private String rawDepartureDate;
    }
}
