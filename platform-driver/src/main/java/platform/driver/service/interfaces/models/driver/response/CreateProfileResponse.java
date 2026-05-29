package platform.driver.service.interfaces.models.driver.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import platform.core.common.service.api.BaseResponse;

@Getter
@Setter
@SuperBuilder
public class CreateProfileResponse extends BaseResponse<CreateProfileResponse.CreateProfileResponseData> {
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    public static class CreateProfileResponseData {
        private String userId;
        private String driverCode;
        private String employeeCode;
        private String currentRouteId;
        private String emergencyContactName;
        private String emergencyContactPhone;
        private String status;
        private String operationStatus;
        private double rating;
        private int totalTrips;
        private String licenseClass;
        private String licenseNumber;
        private String licenseIssueDate;
        private String licenseExpiryDate;
        private long pointsDelta;
        private String pointsReason;
        private boolean kycVerified;
        private boolean trainingCompleted;
        private String note;
    }
}
