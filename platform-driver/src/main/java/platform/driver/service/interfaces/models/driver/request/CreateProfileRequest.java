package platform.driver.service.interfaces.models.driver.request;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import platform.core.common.service.api.BaseRequest;

import static platform.driver.service.interfaces.models.constant.ApplicationConstant.DATE_MONTH_YEAR_REGEX;
import static platform.driver.service.interfaces.models.constant.ApplicationConstant.ONLY_CHARACTER_REGEX;
import static platform.driver.service.interfaces.models.constant.ApplicationConstant.ONLY_NUMBER_AND_CHARACTER_DIGITS;
import static platform.driver.service.interfaces.models.constant.ApplicationConstant.ONLY_NUMBER_REGEX;
import static platform.driver.service.interfaces.models.constant.ApplicationConstant.UUID_REGEX;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class CreateProfileRequest extends BaseRequest {

    @Valid
    @NotNull
    private CreateProfileRequestData data;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    public static class CreateProfileRequestData {
        @NotNull
        @NotBlank
        @Size(max = 36, message = "User Id can't exceed 36 characters")
        @Pattern(regexp = UUID_REGEX, message = "UserId must be in xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx")
        private String userId;

        @NotNull
        @NotBlank
        @Size(max = 20, message = "Employee Code can't exceed 20 characters")
        @Pattern(regexp = ONLY_NUMBER_AND_CHARACTER_DIGITS, message = "only number and digits are allowed")
        private String employeeCode;

        private String currentRouteId;

        @NotNull
        @NotBlank
        @Size(max = 100)
        @Pattern(regexp = ONLY_CHARACTER_REGEX, message = "only characters are allowed")
        private String emergencyContactName;

        @NotNull
        @NotBlank
        @Size(max = 15)
        @Pattern(regexp = ONLY_NUMBER_REGEX, message = "only number are allowed")
        private String emergencyContactPhone;

        @NotNull
        @NotBlank
        private String status;

        @Builder.Default
        private double rating = 0.0;

        @Builder.Default
        private int totalTrips = 0;

        @NotNull
        @NotBlank
        private String licenseClass;

        @NotNull
        @NotBlank
        private String licenseNumber;

        @NotNull
        @NotBlank
        @Pattern(regexp = DATE_MONTH_YEAR_REGEX, message = "must be in format yyyy-mm-dd")
        private String licenseIssueDate;

        @NotNull
        @NotBlank
        @Pattern(regexp = DATE_MONTH_YEAR_REGEX, message = "must be in format yyyy-mm-dd")
        private String licenseExpiryDate;

        @Builder.Default
        private int pointsDelta = 0;

        private String pointsReason;
        private boolean kycVerified;
        private boolean trainingCompleted;
        private String note;
    }
}
