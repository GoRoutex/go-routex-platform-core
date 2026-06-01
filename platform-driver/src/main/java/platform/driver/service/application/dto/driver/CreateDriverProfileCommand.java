package platform.driver.service.application.dto.driver;

import lombok.Builder;
import platform.merchant.service.domain.driver.DriverStatus;

import java.time.LocalDate;

@Builder
public record CreateDriverProfileCommand(
        String userId,
        String employeeCode,
        String emergencyContactName,
        String emergencyContactPhone,
        DriverStatus status,
        double rating,
        int totalTrips,
        String licenseClass,
        String licenseNumber,
        LocalDate licenseIssueDate,
        LocalDate licenseExpiryDate,
        int pointsDelta,
        String pointsReason,
        boolean kycVerified,
        boolean trainingCompleted,
        String note
) {
}
