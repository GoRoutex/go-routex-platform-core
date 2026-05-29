package platform.merchant.service.interfaces.model.driver;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import platform.core.common.service.api.BaseRequest;

import static platform.core.common.service.persistence.constant.RegexConstant.DATE_MONTH_YEAR_REGEX;
import static platform.core.common.service.persistence.constant.RegexConstant.ONLY_NUMBER_REGEX;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class CreateDriverRequest extends BaseRequest {

    @Valid
    @NotNull
    private CreateDriverRequestData data;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    public static class CreateDriverRequestData {
        @NotBlank
        private String creator;

        private String fullName;
        @NotBlank
        private String userId;

        private String employeeCode;
        private String emergencyContactName;
        private String emergencyContactPhone;

        @Pattern(regexp = "(ACTIVE|INACTIVE|SUSPENDED|DELETED)", message = "must be ACTIVE, INACTIVE, SUSPENDED or DELETED")
        private String status;

        @Pattern(regexp = "(ONLINE|OFFLINE|AVAILABLE|NOT_AVAILABLE|BUSY|ON_TRIP)", message = "must be ONLINE, OFFLINE, AVAILABLE, NOT_AVAILABLE, BUSY or ON_TRIP")
        private String operationStatus;

        private String rating;

        @Pattern(regexp = ONLY_NUMBER_REGEX, message = "only digits are allowed for this field")
        private String totalTrips;

        private String licenseClass;
        private String licenseNumber;

        @Pattern(regexp = DATE_MONTH_YEAR_REGEX, message = "must be in yyyy-MM-dd format")
        private String licenseIssueDate;

        @Pattern(regexp = DATE_MONTH_YEAR_REGEX, message = "must be in yyyy-MM-dd format")
        private String licenseExpiryDate;

        @Pattern(regexp = ONLY_NUMBER_REGEX, message = "only digits are allowed for this field")
        private String pointsDelta;

        private String pointsReason;
        private Boolean kycVerified;
        private Boolean trainingCompleted;
        private String note;
    }
}
