package platform.driver.service.application.dto.driver;

import lombok.Builder;
import platform.merchant.service.domain.driver.DriverStatus;
import platform.merchant.service.domain.driver.OperationStatus;

@Builder
public record DeleteDriverProfileResult(
        String driverId,
        DriverStatus status,
        OperationStatus operationStatus
) {
}
