package platform.merchant.service.interfaces.model.assignment;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
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
public class AssignRouteRequest extends BaseRequest {


    @Valid
    @NotNull
    private AssignRouteRequestData data;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    public static class AssignRouteRequestData {

        @NotNull
        @NotBlank
        private String creator;

        @NotNull
        @NotBlank
        private String tripId;

        @NotNull
        @NotBlank
        private String vehicleId;


        @NotNull
        @NotBlank
        private String driverId;
    }
}
