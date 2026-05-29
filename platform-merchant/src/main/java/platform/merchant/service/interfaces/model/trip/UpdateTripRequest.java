package platform.merchant.service.interfaces.model.trip;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import platform.core.common.service.api.BaseRequest;

import java.time.OffsetDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class UpdateTripRequest extends BaseRequest {

    @Valid
    @NotNull
    private UpdateTripRequestData data;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    public static class UpdateTripRequestData {
        @NotNull
        @NotBlank
        private String tripId;
        private String routeId;
        private String pickupBranch;
        private OffsetDateTime departureTime;
        private String rawDepartureTime;
        private String rawDepartureDate;
    }
}
