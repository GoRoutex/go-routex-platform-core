package platform.merchant.service.interfaces.model.vehicle;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import platform.core.common.service.api.BaseResponse;
import platform.core.common.service.domain.vehicle.VehicleStatus;
import platform.core.common.service.domain.vehicle.VehicleTemplateCategory;
import platform.merchant.service.domain.vehicle.VehicleTemplateType;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class AddVehicleResponse extends BaseResponse<AddVehicleResponse.AddVehicleResponseData> {
    @Getter
    @Setter
    @NoArgsConstructor
    @SuperBuilder
    public static class AddVehicleResponseData {
        private String id;
        private String templateId;
        private String creator;
        private VehicleTemplateCategory category;
        private VehicleTemplateType type;
        private String vehiclePlate;
        private Long seatCapacity;
        private String manufacturer;
        private VehicleStatus status;
    }
}
