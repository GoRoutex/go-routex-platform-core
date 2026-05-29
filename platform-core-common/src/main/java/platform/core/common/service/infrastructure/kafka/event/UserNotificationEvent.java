package platform.core.common.service.infrastructure.kafka.event;

import lombok.Builder;

@Builder
public record UserNotificationEvent(
        String merchantId,
        String userEmail,
        String title,
        String message,
        String notificationType,
        String referenceId
) {
}
