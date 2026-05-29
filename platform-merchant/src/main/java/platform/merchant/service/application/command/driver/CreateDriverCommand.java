package platform.merchant.service.application.command.driver;

import lombok.Builder;
import platform.core.common.service.common.RequestContext;

@Builder
public record CreateDriverCommand(
        RequestContext context,
        String merchantId,
        String creator,
        String userId,
        String fullName,
        String employeeCode,
        String emergencyContactName,
        String emergencyContactPhone,
        String status,
        String operationStatus,
        String rating,
        String totalTrips,
        String licenseClass,
        String licenseNumber,
        String licenseIssueDate,
        String licenseExpiryDate,
        String pointsDelta,
        String pointsReason,
        Boolean kycVerified,
        Boolean trainingCompleted,
        String note) {
}
