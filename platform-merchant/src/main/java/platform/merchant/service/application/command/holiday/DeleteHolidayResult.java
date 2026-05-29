package platform.merchant.service.application.command.holiday;

import lombok.Builder;

@Builder
public record DeleteHolidayResult(
        String id,
        String status
) {}
