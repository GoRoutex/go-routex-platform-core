package platform.core.common.service.infrastructure.kafka.activity;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import platform.core.common.service.infrastructure.kafka.config.KafkaEventPublisher;
import vn.com.go.routex.identity.security.log.SystemLog;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class RecentActivityPublisher {

    private static final String AUDIENCE_ADMIN = "ADMIN";
    private static final String AUDIENCE_MERCHANT = "MERCHANT";
    private static final String SCOPE_SYSTEM = "SYSTEM";
    private static final String SCOPE_MERCHANT = "MERCHANT";
    private static final String VISIBILITY_ADMIN_ONLY = "ADMIN_ONLY";
    private static final String VISIBILITY_MERCHANT_ONLY = "MERCHANT_ONLY";

    private final KafkaEventPublisher kafkaEventPublisher;
    private final SystemLog sLog = SystemLog.getLogger(this.getClass());

    @Value("${spring.kafka.topics.activities:recent.activities.event}")
    private String recentActivitiesTopic;

    public void publishAdminActivity(
            String eventKey,
            String aggregateId,
            String severity,
            String status,
            String sourceService,
            String correlationId,
            String title,
            String message,
            String actorUserId,
            String actorName,
            String entityType,
            String entityId,
            String entityDisplayName,
            Map<String, Object> metadata
    ) {
        publish(
                AUDIENCE_ADMIN,
                SCOPE_SYSTEM,
                null,
                VISIBILITY_ADMIN_ONLY,
                null,
                eventKey,
                aggregateId,
                severity,
                status,
                sourceService,
                correlationId,
                title,
                message,
                actorUserId,
                actorName,
                entityType,
                entityId,
                entityDisplayName,
                metadata
        );
    }

    public void publishMerchantActivity(
            String merchantId,
            String eventKey,
            String aggregateId,
            String severity,
            String status,
            String sourceService,
            String correlationId,
            String title,
            String message,
            String actorUserId,
            String actorName,
            String entityType,
            String entityId,
            String entityDisplayName,
            Map<String, Object> metadata
    ) {
        publish(
                AUDIENCE_MERCHANT,
                SCOPE_MERCHANT,
                merchantId,
                VISIBILITY_MERCHANT_ONLY,
                merchantId,
                eventKey,
                aggregateId,
                severity,
                status,
                sourceService,
                correlationId,
                title,
                message,
                actorUserId,
                actorName,
                entityType,
                entityId,
                entityDisplayName,
                metadata
        );
    }

    private void publish(
            String audienceType,
            String scopeType,
            String scopeId,
            String visibility,
            String merchantId,
            String eventKey,
            String aggregateId,
            String severity,
            String status,
            String sourceService,
            String correlationId,
            String title,
            String message,
            String actorUserId,
            String actorName,
            String entityType,
            String entityId,
            String entityDisplayName,
            Map<String, Object> metadata
    ) {
        try {
            kafkaEventPublisher.publishRecentActivity(
                    recentActivitiesTopic,
                    audienceType,
                    scopeType,
                    scopeId,
                    visibility,
                    severity,
                    status,
                    sourceService,
                    correlationId,
                    merchantId,
                    eventKey,
                    aggregateId,
                    title,
                    message,
                    actorUserId,
                    actorName,
                    entityType,
                    entityId,
                    entityDisplayName,
                    metadata
            );
        } catch (Exception ex) {
            sLog.warn("[RECENT-ACTIVITY-PUBLISH-FAILED] eventKey={}, aggregateId={}, message={}",
                    eventKey, aggregateId, ex.getMessage());
        }
    }
}
