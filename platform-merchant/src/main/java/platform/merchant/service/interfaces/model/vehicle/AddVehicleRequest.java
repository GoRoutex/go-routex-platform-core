package platform.merchant.service.interfaces.model.vehicle;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import platform.core.common.service.api.BaseRequest;

import static platform.core.common.service.persistence.constant.RegexConstant.VEHICLE_PLATE_REGEX;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class AddVehicleRequest extends BaseRequest {

    @Valid
    @NotNull
    private AddVehicleRequestData data;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    public static class AddVehicleRequestData {
        @NotNull
        @NotBlank
        private String creator;

        @NotNull
        @NotBlank
        private String templateId;

        @NotNull
        @NotBlank
        @Pattern(regexp = VEHICLE_PLATE_REGEX, message = "must be in format of Vietnamese Plate e.g: 51F1-268.99")
        private String vehiclePlate;
    }
}
