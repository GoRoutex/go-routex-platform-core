package platform.driver.service.interfaces.models.driver.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import platform.core.common.service.api.BaseResponse;
import platform.merchant.service.domain.driver.DriverStatus;

import java.time.LocalDate;

@Getter
@Setter
@SuperBuilder
public class UpdateProfileResponse extends BaseResponse<UpdateProfileResponse.UpdateProfileResponseData> {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    public static class UpdateProfileResponseData {
        private String driverId;
        private String employeeCode;
        private String emergencyContactName;
        private String emergencyContactPhone;
        private DriverStatus status;
        private String licenseNumber;
        private String licenseClass;
        private LocalDate licenseIssueDate;
        private LocalDate licenseExpiryDate;
        private Integer pointsDelta;
        private String pointsReason;
        private Boolean kycVerified;
        private Boolean trainingCompleted;
        private String note;
    }
}
