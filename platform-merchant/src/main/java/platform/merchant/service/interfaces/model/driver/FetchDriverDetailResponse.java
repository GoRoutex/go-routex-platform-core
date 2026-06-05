package platform.merchant.service.interfaces.model.driver;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import platform.core.common.service.api.BaseResponse;
import platform.merchant.service.domain.driver.DriverStatus;
import platform.merchant.service.domain.driver.OperationStatus;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class FetchDriverDetailResponse extends BaseResponse<FetchDriverDetailResponse.FetchDriverDetailResponseData> {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    public static class FetchDriverDetailResponseData {
        private String id;
        private FetchDriverDetailMerchantInfo merchantInfo;
        private FetchDriverDetailUserInfo userInfo;
        private String employeeCode;
        private String emergencyContactName;
        private String emergencyContactPhone;
        private DriverStatus status;
        private OperationStatus operationStatus;
        private Double rating;
        private Integer totalTrips;
        private String licenseClass;
        private String licenseNumber;
        private LocalDate licenseIssueDate;
        private LocalDate licenseExpiryDate;
        private Integer pointsDelta;
        private String pointsReason;
        private Boolean kycVerified;
        private Boolean trainingCompleted;
        private String note;
    }


    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    public static class FetchDriverDetailMerchantInfo {
        private String merchantId;
        private String merchantName;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    public static class FetchDriverDetailUserInfo {
        private String userId;
        private String phone;
        private String email;
        private String fullName;
    }
}
