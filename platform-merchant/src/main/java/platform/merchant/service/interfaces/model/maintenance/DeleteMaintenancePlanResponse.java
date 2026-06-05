package platform.merchant.service.interfaces.model.maintenance;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import platform.core.common.service.api.BaseResponse;
import platform.merchant.service.domain.maintenance.MaintenancePlanStatus;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class DeleteMaintenancePlanResponse extends BaseResponse<DeleteMaintenancePlanResponse.DeleteMaintenancePlanResponseData> {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    public static class DeleteMaintenancePlanResponseData {
        private String id;
        private String code;
        private MaintenancePlanStatus status;
    }
}
