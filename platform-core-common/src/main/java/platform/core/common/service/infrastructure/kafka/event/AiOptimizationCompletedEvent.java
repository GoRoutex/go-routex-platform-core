package platform.core.common.service.infrastructure.kafka.event;

import lombok.Builder;

@Builder
public record AiOptimizationCompletedEvent(
        String jobId,
        String merchantId,
        String routeId,
        String recommendationsPayload,
        String status,
        String errorMessage
) {
}
