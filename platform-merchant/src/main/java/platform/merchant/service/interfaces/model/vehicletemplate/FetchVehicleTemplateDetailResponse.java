package platform.merchant.service.interfaces.model.vehicletemplate;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import platform.core.common.service.domain.vehicle.FuelType;
import platform.core.common.service.domain.vehicle.VehicleTemplateCategory;
import platform.core.common.service.domain.vehicle.VehicleTemplateStatus;
import platform.core.common.service.domain.vehicle.VehicleTemplateType;
import platform.core.common.service.api.BaseResponse;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class FetchVehicleTemplateDetailResponse extends BaseResponse<FetchVehicleTemplateDetailResponse.FetchVehicleTemplateDetailResponseData> {

    @Getter
    @Setter
    @NoArgsConstructor
    @SuperBuilder
    public static class FetchVehicleTemplateDetailResponseData {
        private String id;
        private String merchantId;
        private String code;
        private String name;
        private String manufacturer;
        private String model;
        private Long seatCapacity;
        private VehicleTemplateCategory category;
        private VehicleTemplateType type;
        private FuelType fuelType;
        private Boolean hasFloor;
        private BigDecimal ticketPrice;
        private VehicleTemplateStatus status;
    }
}
