package platform.merchant.service.application.command.holiday;

import lombok.Builder;
import platform.core.common.service.common.RequestContext;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
public record UpdateHolidayCommand(
        String id,
        LocalDate holidayDate,
        String name,
        Boolean isPeakDay,
        BigDecimal surchargeRate,
        String description,
        RequestContext context
) {}
