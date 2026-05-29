package platform.driver.service.interfaces.models.driver.request;

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

import static platform.driver.service.interfaces.models.constant.ApplicationConstant.DATE_MONTH_YEAR_REGEX;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class UpdateProfileRequest extends BaseRequest {

    @Valid
    @NotNull
    private UpdateProfileRequestData data;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    public static class UpdateProfileRequestData {
        @NotBlank
        @NotNull
        private String driverId;

        // Generate if empty or null;
        private String employeeCode;
        @NotBlank
        @NotNull
        private String emergencyContactName;
        @NotBlank
        @NotNull
        private String emergencyContactPhone;
        private String status;
        @NotBlank
        @NotNull
        private String licenseNumber;

        @NotBlank
        @NotNull
        private String licenseClass;

        @NotNull
        @NotBlank
        @Pattern(regexp = DATE_MONTH_YEAR_REGEX, message = "must be in format yyyy-mm-dd")
        private String licenseIssueDate;

        @NotBlank
        @NotNull
        @Pattern(regexp = DATE_MONTH_YEAR_REGEX, message = "must be in format yyyy-mm-dd")
        private String licenseExpiryDate;

        private Integer pointsDelta;
        private String pointsReason;
        private Boolean kycVerified;
        private Boolean trainingCompleted;

        private String note;
    }
}
