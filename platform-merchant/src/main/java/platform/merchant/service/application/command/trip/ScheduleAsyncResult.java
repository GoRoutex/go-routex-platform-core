package platform.merchant.service.application.command.trip;

import lombok.Builder;

@Builder
public record ScheduleAsyncResult(
        String jobId,
        String status
) {
}
