package platform.merchant.service.application.command.driver;

import lombok.Builder;
import platform.merchant.service.domain.driver.DriverStatus;
import platform.merchant.service.domain.driver.OperationStatus;

@Builder
public record DeleteDriverResult(
        String id,
        DriverStatus status,
        OperationStatus operationStatus
) {
}
