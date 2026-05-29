package platform.merchant.service.interfaces.model.driver;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import platform.merchant.service.domain.driver.DriverStatus;
import platform.merchant.service.domain.driver.OperationStatus;
import platform.core.common.service.api.BaseResponse;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class UpdateDriverResponse extends BaseResponse<UpdateDriverResponse.UpdateDriverResponseData> {
    @Getter
    @Setter
    @NoArgsConstructor
    @SuperBuilder
    public static class UpdateDriverResponseData {
        private String id;
        private String merchantId;
        private String creator;
        private String userId;
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
}
