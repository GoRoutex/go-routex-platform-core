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


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class CreateTripRequest extends BaseRequest {


    @Valid
    @NotNull
    private String creator;

    @Valid
    @NotNull
    private CreateTripRequestData data;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    public static class CreateTripRequestData {
        private String routeId;
        private OffsetDateTime departureTime;
        private String rawDepartureTime;
        private String rawDepartureDate;
    }
}
