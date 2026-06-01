package platform.driver.service.application.dto.driver;

import lombok.Builder;
import platform.merchant.service.domain.driver.DriverStatus;
import platform.merchant.service.domain.driver.OperationStatus;

@Builder
public record CreateDriverProfileResult(
        String userId,
        String driverCode,
        String employeeCode,
        String emergencyContactName,
        String emergencyContactPhone,
        DriverStatus status,
        OperationStatus operationStatus,
        Double rating,
        Integer totalTrips,
        String licenseClass,
        String licenseNumber,
        String licenseIssueDate,
        String licenseExpiryDate,
        Integer pointsDelta,
        String pointsReason,
        Boolean kycVerified,
        Boolean trainingCompleted,
        String note
) {
}
