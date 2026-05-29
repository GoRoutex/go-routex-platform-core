package platform.core.common.service.infrastructure.kafka.event;

import lombok.Builder;

import java.util.Map;

@Builder
public record EmailNotificationEvent(
        String toEmail,
        String subject,
        String templateName,
        Map<String, Object> variables
) {
}
