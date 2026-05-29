package platform.merchant.service.interfaces.model.holiday.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import platform.core.common.service.api.BaseRequest;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class CreateHolidayRequest extends BaseRequest {
    private CreateHolidayData data;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    public static class CreateHolidayData {
        @NotNull(message = "Holiday date is required")
        private LocalDate holidayDate;

        @NotBlank(message = "Holiday name is required")
        private String name;

        @NotNull(message = "Peak day flag is required")
        private Boolean isPeakDay;

        private BigDecimal surchargeRate;
        private String description;
    }
}
