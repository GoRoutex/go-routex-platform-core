package platform.merchant.service.application.command.driver;

import lombok.Builder;
import platform.merchant.service.domain.driver.DriverStatus;
import platform.merchant.service.domain.driver.OperationStatus;

import java.time.LocalDate;
import java.util.List;

@Builder
public record FetchDriversResult(
        List<FetchDriverItemResult> items,
        int pageNumber,
        int pageSize,
        long totalElements,
        int totalPages
) {
    @Builder
    public record FetchDriverItemResult(
            String id,
            FetchDriverMerchantInfo merchantInfo,
            FetchDriverUserInfo userInfo,
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
        public record FetchDriverMerchantInfo(
                String merchantId,
                String merchantName
        ) {

        }

        @Builder
        public record FetchDriverUserInfo(
                String userId,
                String fullName,
                String phone,
                String email
        ) {

        }
    }
}
