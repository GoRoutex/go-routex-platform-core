package platform.driver.service.interfaces.models.driver.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import platform.core.common.service.api.BaseResponse;
import platform.merchant.service.domain.driver.DriverStatus;
import platform.merchant.service.domain.driver.OperationStatus;

@Getter
@Setter
@SuperBuilder
public class UpdateDriverStatusResponse extends BaseResponse<UpdateDriverStatusResponse.UpdateDriverStatusResponseData> {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    public static class UpdateDriverStatusResponseData {
        private String driverId;
        private DriverStatus status;
        private OperationStatus operationStatus;
    }
}
