package platform.merchant.service.application.command.trip;

import lombok.Builder;
import platform.core.common.service.common.RequestContext;

@Builder
public record FetchScheduleOptimizationJobQuery(
        RequestContext context,
        String merchantId,
        String jobId
) {
}
