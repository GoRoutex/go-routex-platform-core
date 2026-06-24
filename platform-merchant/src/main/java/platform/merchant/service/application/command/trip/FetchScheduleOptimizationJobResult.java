package platform.merchant.service.application.command.trip;

import lombok.Builder;

@Builder
public record FetchScheduleOptimizationJobResult(
        String jobId,
        String merchantId,
        String routeId,
        String status,
        String recommendationsPayload,
        String creatorEmail
) {
}
