package platform.driver.service.application.dto.driver;


import platform.merchant.service.domain.driver.DriverStatus;
import platform.merchant.service.domain.driver.OperationStatus;

public record UpdateDriverStatusCommand(
        String driverId,
        DriverStatus status,
        OperationStatus operationStatus
) {
}
