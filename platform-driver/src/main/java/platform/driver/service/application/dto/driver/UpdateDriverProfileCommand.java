package platform.driver.service.application.dto.driver;


import platform.merchant.service.domain.driver.DriverStatus;

import java.time.LocalDate;

public record UpdateDriverProfileCommand(
        String driverId,
        String employeeCode,
        String emergencyContactName,
        String emergencyContactPhone,
        DriverStatus status,
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
