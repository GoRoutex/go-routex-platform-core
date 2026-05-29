package platform.driver.service.application.dto.driver;

import lombok.Builder;
import platform.merchant.service.domain.driver.DriverStatus;
import platform.merchant.service.domain.driver.OperationStatus;

@Builder
public record UpdateDriverStatusResult(
        String driverId,
        DriverStatus status,
        OperationStatus operationStatus
) {
}
