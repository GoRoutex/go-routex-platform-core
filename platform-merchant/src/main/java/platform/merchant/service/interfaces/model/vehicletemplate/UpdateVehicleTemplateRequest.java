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
import platform.core.common.service.domain.vehicle.VehicleTemplateType;
import platform.core.common.service.api.BaseRequest;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class UpdateVehicleTemplateRequest extends BaseRequest {

    @Valid
    @NotNull
    private UpdateVehicleTemplateRequestData data;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    public static class UpdateVehicleTemplateRequestData {
        @NotBlank
        private String creator;
        @NotBlank
        private String templateId;
        private String code;
        private String name;
        private String manufacturer;
        private String model;
        @Min(1)
        private Long seatCapacity;
        private VehicleTemplateCategory category;
        private VehicleTemplateType type;
        private FuelType fuelType;
        private Boolean hasFloor;
        @DecimalMin(value = "0.0", inclusive = true)
        private BigDecimal ticketPrice;
        private VehicleTemplateStatus status;
    }
}
