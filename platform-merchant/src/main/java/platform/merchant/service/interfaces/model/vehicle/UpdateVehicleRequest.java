package platform.merchant.service.interfaces.model.vehicle;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import platform.core.common.service.api.BaseRequest;
import platform.core.common.service.domain.vehicle.VehicleStatus;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class UpdateVehicleRequest extends BaseRequest {

    @Valid
    @NotNull
    private UpdateVehicleRequestData data;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    public static class UpdateVehicleRequestData {
        @NotNull
        @NotBlank
        private String creator;
        @NotNull
        @NotBlank
        private String vehicleId;
        private String templateId;
        private String vehiclePlate;
        private VehicleStatus status;
    }
}
