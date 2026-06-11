package platform.management.service.interfaces.models.trip;

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

import static platform.core.common.service.persistence.constant.RegexConstant.ONLY_NUMBER_REGEX;
import static platform.core.common.service.persistence.constant.RegexConstant.YEAR_MONTH_DATE_REGEX;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class SearchRoundTripRequest extends BaseRequest {

    @Valid
    @NotNull
    private SearchRoundTripRequestData data;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    public static class SearchRoundTripRequestData {

        @Pattern(regexp = ONLY_NUMBER_REGEX, message = "only digits are allowed for this field")
        private String seat;

        @Pattern(regexp = ONLY_NUMBER_REGEX, message = "only digits are allowed for this field")
        private String pageSize;

        @Pattern(regexp = ONLY_NUMBER_REGEX, message = "only digits are allowed for this field")
        private String pageNumber;

        @Valid
        @NotNull
        private SearchRoundTripDetailData outboundData;

        @Valid
        @NotNull
        private SearchRoundTripDetailData returnData;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    public static class SearchRoundTripDetailData {

        @NotNull
        @NotBlank
        @Pattern(regexp = "^[\\p{L}0-9 ]+$", message = "only characters, numbers and spaces are allowed for this field")
        private String origin;

        @NotNull
        @NotBlank
        @Pattern(regexp = "^[\\p{L}0-9 ]+$", message = "only characters, numbers and spaces are allowed for this field")
        private String destination;

        @NotNull
        @NotBlank
        @Pattern(regexp = YEAR_MONTH_DATE_REGEX, message = "must be in format of yyyy-MM-dd")
        private String departureDate;
    }
}
