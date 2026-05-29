package platform.booking.service.interfaces.models.seat;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import platform.core.common.service.api.BaseRequest;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class HoldSeatRequest extends BaseRequest {

    private String creator;

    @Valid
    @NotNull
    private HoldSeatRequestData data;
    @Valid
    @NotNull
    private HoldSeatRequestInformation info;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    public static class HoldSeatRequestInformation {
        private String customerName;
        private String customerPhone;
        private String customerEmail;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    public static class HoldSeatRequestData {
        @NotBlank
        @NotNull
        private String tripId;

        @NotEmpty
        private List<String> seatNos;

        private String holdBy;

        private String pickupType;
        private String pickupStopId;
        private String pickupAddress;
        private String dropoffType;
        private String dropoffStopId;
        private String dropoffAddress;
    }
}
