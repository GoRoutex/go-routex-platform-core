package platform.merchant.service.application.command.driver;

import lombok.Builder;
import platform.merchant.service.domain.driver.DriverStatus;
import platform.merchant.service.domain.driver.OperationStatus;

import java.time.LocalDate;

@Builder
public record FetchDriverDetailResult(
        String id,
        FetchDriverDetailMerchantInfo merchantInfo,
        FetchDriverDetailUserInfo userInfo,
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

    @Builder
    public record FetchDriverDetailMerchantInfo(
        String merchantId,
        String merchantName
    ) {}

    @Builder
    public record FetchDriverDetailUserInfo(
            String userId,
            String phone,
            String fullName,
            String email
    ) {

    }
}
