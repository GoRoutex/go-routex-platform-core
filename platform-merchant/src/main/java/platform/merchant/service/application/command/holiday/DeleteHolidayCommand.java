package platform.merchant.service.application.command.holiday;

import lombok.Builder;
import platform.core.common.service.common.RequestContext;

@Builder
public record DeleteHolidayCommand(
        String id,
        RequestContext context
) {}
