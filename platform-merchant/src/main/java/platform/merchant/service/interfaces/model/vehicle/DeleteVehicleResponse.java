package platform.merchant.service.interfaces.model.vehicle;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import platform.core.common.service.api.BaseResponse;
import platform.core.common.service.domain.vehicle.VehicleStatus;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class DeleteVehicleResponse extends BaseResponse<DeleteVehicleResponse.DeleteVehicleResponseData> {

    @Getter
    @Setter
    @NoArgsConstructor
    @SuperBuilder
    public static class DeleteVehicleResponseData {
        private String id;
        private VehicleStatus status;
    }
}

