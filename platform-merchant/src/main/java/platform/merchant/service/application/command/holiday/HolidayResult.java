package platform.merchant.service.application.command.holiday;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
public record HolidayResult(
        String id,
        LocalDate holidayDate,
        String name,
        Boolean isPeakDay,
        BigDecimal surchargeRate,
        String description
) {}
