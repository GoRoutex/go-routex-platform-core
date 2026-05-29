package platform.merchant.service.interfaces.model.driver;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import platform.merchant.service.domain.driver.DriverStatus;
import platform.merchant.service.domain.driver.OperationStatus;
import platform.core.common.service.api.BaseResponse;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class DeleteDriverResponse extends BaseResponse<DeleteDriverResponse.DeleteDriverResponseData> {
    @Getter
    @Setter
    @NoArgsConstructor
    @SuperBuilder
    public static class DeleteDriverResponseData {
        private String id;
        private DriverStatus status;
        private OperationStatus operationStatus;
    }
}
