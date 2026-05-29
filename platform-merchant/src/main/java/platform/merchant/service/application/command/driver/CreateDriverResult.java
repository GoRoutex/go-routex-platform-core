package platform.merchant.service.application.command.driver;

import lombok.Builder;
import platform.merchant.service.domain.driver.DriverStatus;
import platform.merchant.service.domain.driver.OperationStatus;

import java.time.LocalDate;

@Builder
public record CreateDriverResult(
        String id,
        String merchantId,
        String creator,
        String userId,
        String employeeCode,
        String emergencyContactName,
        String emergencyContactPhone,
        DriverStatus status,
        OperationStatus operationStatus,
        Double rating,
        Integer totalTrips,
        String licenseClass,
        String licenseNumber,
        LocalDate licenseIssueDate,
        LocalDate licenseExpiryDate,
        Integer pointsDelta,
        String pointsReason,
        Boolean kycVerified,
        Boolean trainingCompleted,
        String note
) {
}
