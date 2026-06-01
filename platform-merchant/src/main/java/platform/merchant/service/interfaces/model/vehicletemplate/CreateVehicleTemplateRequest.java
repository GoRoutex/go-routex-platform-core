package platform.merchant.service.interfaces.model.vehicletemplate;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import platform.core.common.service.domain.vehicle.FuelType;
import platform.core.common.service.domain.vehicle.VehicleTemplateCategory;
import platform.core.common.service.domain.vehicle.VehicleTemplateStatus;
import platform.core.common.service.api.BaseRequest;
import platform.merchant.service.domain.vehicle.VehicleTemplateType;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class CreateVehicleTemplateRequest extends BaseRequest {

    @Valid
    @NotNull
    private CreateVehicleTemplateRequestData data;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    public static class CreateVehicleTemplateRequestData {
        @NotBlank
        private String creator;
        @NotBlank
        private String code;
        @NotBlank
        private String name;
        @NotBlank
        private String manufacturer;
        @NotBlank
        private String model;
        @NotNull
        @Min(1)
        private Long seatCapacity;
        @NotNull
        private VehicleTemplateCategory category;
        @NotNull
        private VehicleTemplateType type;
        @NotNull
        private FuelType fuelType;
        @NotNull
        private Boolean hasFloor;
        @NotNull
        @DecimalMin(value = "0.0", inclusive = true)
        private BigDecimal ticketPrice;
        private VehicleTemplateStatus status;
    }
}
