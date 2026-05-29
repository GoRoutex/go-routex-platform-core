package platform.merchant.service.interfaces.model.route;


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

import static platform.core.common.service.persistence.constant.RegexConstant.HOUR_MINUTES_REGEX;
import static platform.core.common.service.persistence.constant.RegexConstant.ONLY_CHARACTER_REGEX;
import static platform.core.common.service.persistence.constant.RegexConstant.ONLY_NUMBER_REGEX;
import static platform.core.common.service.persistence.constant.RegexConstant.YEAR_MONTH_DATE_REGEX;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class SearchRouteRequest extends BaseRequest {

    @Valid
    @NotNull
    private SearchRouteRequestData data;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    public static class SearchRouteRequestData {

        @NotNull
        @NotBlank
        @Pattern(regexp = ONLY_CHARACTER_REGEX, message = "only characters are allowed for this field")
        private String origin;

        @NotNull
        @NotBlank
        @Pattern(regexp = ONLY_CHARACTER_REGEX, message = "only characters are allowed for this field")
        private String destination;

        @Pattern(regexp = YEAR_MONTH_DATE_REGEX, message = "must be in format of yyyy-MM-dd")
        private String departureDate;

        @Pattern(regexp = ONLY_NUMBER_REGEX, message = "only digits are allowed for this field")
        private String seat;

        @Pattern(regexp = HOUR_MINUTES_REGEX, message = "must be in format of HH:mm")
        private String fromTime;

        @Pattern(regexp = HOUR_MINUTES_REGEX, message = "must be in format of HH:mm")
        private String toTime;

        @Pattern(regexp = ONLY_NUMBER_REGEX, message = "only digits are allowed for this field")
        private String pageSize;

        @Pattern(regexp = ONLY_NUMBER_REGEX, message = "only digits are allowed for this field")
        private String pageNumber;
    }
}
