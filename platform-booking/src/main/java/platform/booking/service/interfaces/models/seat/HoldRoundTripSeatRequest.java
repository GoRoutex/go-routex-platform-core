package platform.booking.service.interfaces.models.seat;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import platform.core.common.service.api.BaseRequest;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class HoldRoundTripSeatRequest extends BaseRequest {

    private String creator;

    @Valid
    @NotNull
    private HoldRoundTripSeatRequestData data;

    @Valid
    @NotNull
    private HoldSeatRequest.HoldSeatRequestInformation info;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    public static class HoldRoundTripSeatRequestData {
        @Valid
        @NotNull
        private HoldSeatRequest.HoldSeatRequestData outboundTrip;

        @Valid
        @NotNull
        private HoldSeatRequest.HoldSeatRequestData returnTrip;

        private String holdBy;
    }
}
