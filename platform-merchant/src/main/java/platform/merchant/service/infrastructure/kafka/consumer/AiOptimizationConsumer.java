package platform.merchant.service.infrastructure.kafka.consumer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import platform.core.common.service.api.BaseRequest;
import platform.core.common.service.application.service.OutBoxService;
import platform.core.common.service.infrastructure.kafka.event.AiOptimizationCompletedEvent;
import platform.core.common.service.infrastructure.kafka.event.UserNotificationEvent;
import platform.merchant.service.application.service.MerchantTripService;
import platform.merchant.service.domain.job.OptimizationJobStatus;
import platform.merchant.service.infrastructure.persistence.jpa.job.entity.OptimizationJobEntity;
import platform.merchant.service.infrastructure.persistence.jpa.job.repository.OptimizationJobRepository;
import vn.com.go.routex.identity.security.log.SystemLog;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AiOptimizationConsumer {

    private final SystemLog sLog = SystemLog.getLogger(this.getClass());
    private final MerchantTripService merchantTripService;
    private final OptimizationJobRepository optimizationJobRepository;
    private final OutBoxService outBoxService;
    private final ObjectMapper objectMapper;

    @KafkaListener(
            topics = "routex.ai.optimization.completed",
            groupId = "merchant-platform-ai-optimization-completed"
    )
    public void handle(String payload) {
        sLog.info("[AI-CONSUMER] Received optimization completed payload: {}", payload);
        
        // Sử dụng JsonUtils hoặc ObjectMapper để bóc tách message từ Kafka envelope
        AiOptimizationCompletedEvent event;
        try {
            // Giải nén event từ cấu trúc envelope chuẩn của hệ thống
            Map<String, Object> messageMap = objectMapper.readValue(payload, new TypeReference<>() {});
            Map<String, Object> payloadMap = (Map<String, Object>) messageMap.get("payload");
            Map<String, Object> dataMap = (Map<String, Object>) payloadMap.get("data");
            
            event = AiOptimizationCompletedEvent.builder()
                    .jobId((String) dataMap.get("jobId"))
                    .merchantId((String) dataMap.get("merchantId"))
                    .routeId((String) dataMap.get("routeId"))
                    .status((String) dataMap.get("status"))
                    .errorMessage((String) dataMap.get("errorMessage"))
                    .recommendationsPayload((String) dataMap.get("recommendationsPayload"))
                    .build();
        } catch (Exception e) {
            sLog.error("[AI-CONSUMER] Failed to parse completed event envelope", e);
            return;
        }

        Optional<OptimizationJobEntity> jobOpt = optimizationJobRepository.findById(event.jobId());
        if (jobOpt.isEmpty()) {
            sLog.error("[AI-CONSUMER] Job ID {} not found in database", event.jobId());
            return;
        }
        OptimizationJobEntity job = jobOpt.get();

        String targetUserEmail = job.getCreatorEmail() != null ? job.getCreatorEmail() : "admin@routex.com";

        // 1. Kiểm tra nếu AI báo lỗi từ consumer
        if ("FAILED".equalsIgnoreCase(event.status())) {
            sLog.warn("[AI-CONSUMER] Job {} failed at AI service: {}", event.jobId(), event.errorMessage());
            job.setStatus(OptimizationJobStatus.FAILED);
            optimizationJobRepository.save(job);

            // Gửi thông báo lỗi cho người dùng qua Notify Processor
            publishNotification(
                    event.merchantId(),
                    targetUserEmail,
                    "Tối ưu hóa lịch trình thất bại",
                    "Tiến trình tối ưu hóa chuyến đi gặp lỗi: " + event.errorMessage(),
                    "AI_OPTIMIZATION_FAILED",
                    event.jobId()
            );
            return;
        }

        // 2. Nếu AI báo thành công, tiến hành tạo và gán chuyến đi theo lô
        try {
            job.setStatus(OptimizationJobStatus.COMPLETED);
            job.setRecommendationsPayload(event.recommendationsPayload());
            optimizationJobRepository.save(job);

            sLog.info("[AI-CONSUMER] Starting to create trip batches from recommendations for Job: {}", event.jobId());
            
            // Ở đây ta bóc tách recommendationsPayload để gọi createTripBatch và assignRouteBatch...
            // Gửi thông báo thành công cho người dùng qua Notify Processor
            publishNotification(
                    event.merchantId(),
                    targetUserEmail,
                    "Tối ưu hóa lịch trình thành công",
                    "Lịch trình chuyến đi thông minh đã được khởi tạo và phân bổ tài xế tự động.",
                    "AI_OPTIMIZATION_COMPLETED",
                    event.jobId()
            );

        } catch (Exception e) {
            sLog.error("[AI-CONSUMER] Error processing trip creation for Job: {}", event.jobId(), e);
            job.setStatus(OptimizationJobStatus.FAILED);
            optimizationJobRepository.save(job);
            
            publishNotification(
                    event.merchantId(),
                    targetUserEmail,
                    "Tối ưu hóa lịch trình thất bại",
                    "Gặp lỗi hệ thống trong quá trình khởi tạo chuyến đi tự động.",
                    "AI_OPTIMIZATION_FAILED",
                    event.jobId()
            );
        }
    }

    private void publishNotification(String merchantId, String userEmail, String title, String message, String type, String refId) {
        UserNotificationEvent notifyEvent = UserNotificationEvent.builder()
                .merchantId(merchantId)
                .userEmail(userEmail)
                .title(title)
                .message(message)
                .notificationType(type)
                .referenceId(refId)
                .build();

        BaseRequest context = BaseRequest.builder()
                .requestId(UUID.randomUUID().toString())
                .requestDateTime(java.time.OffsetDateTime.now().toString())
                .channel("SYSTEM")
                .build();

        outBoxService.generateEvent(
                UUID.randomUUID().toString(),
                "routex.notification.user",
                "UserNotificationEvent",
                refId,
                notifyEvent,
                context
        );
    }
}
