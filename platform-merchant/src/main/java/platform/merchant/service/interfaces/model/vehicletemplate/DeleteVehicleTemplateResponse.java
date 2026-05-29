package platform.merchant.service.interfaces.model.vehicletemplate;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import platform.core.common.service.domain.vehicle.VehicleTemplateStatus;
import platform.core.common.service.api.BaseResponse;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class DeleteVehicleTemplateResponse extends BaseResponse<DeleteVehicleTemplateResponse.DeleteVehicleTemplateResponseData> {

    @Getter
    @Setter
    @NoArgsConstructor
    @SuperBuilder
    public static class DeleteVehicleTemplateResponseData {
        private String id;
        private String code;
        private VehicleTemplateStatus status;
    }
}
