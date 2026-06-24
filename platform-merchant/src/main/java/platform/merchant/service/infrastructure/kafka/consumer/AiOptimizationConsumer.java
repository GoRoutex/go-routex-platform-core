package platform.merchant.service.infrastructure.kafka.consumer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import platform.core.common.service.api.BaseRequest;
import platform.core.common.service.application.service.OutBoxService;
import platform.core.common.service.common.RequestContext;
import platform.core.common.service.infrastructure.kafka.event.AiOptimizationCompletedEvent;
import platform.core.common.service.infrastructure.kafka.event.UserNotificationEvent;
import platform.merchant.service.application.command.route.AssignRouteBatchCommand;
import platform.merchant.service.application.command.route.AssignRouteBatchResult;
import platform.merchant.service.application.command.trip.CreateTripBatchCommand;
import platform.merchant.service.application.command.trip.CreateTripBatchResult;
import platform.merchant.service.application.service.MerchantTripService;
import platform.merchant.service.domain.job.OptimizationJobStatus;
import platform.merchant.service.infrastructure.persistence.jpa.job.entity.OptimizationJobEntity;
import platform.merchant.service.infrastructure.persistence.jpa.job.repository.OptimizationJobRepository;
import vn.com.go.routex.identity.security.log.SystemLog;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static platform.core.common.service.persistence.constant.ApplicationConstant.DEFAULT_ZONE;

@Component
@Lazy(false)
@RequiredArgsConstructor
public class AiOptimizationConsumer {

    private static final DateTimeFormatter AI_DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter RAW_DEPARTURE_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

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
            if (isAlreadyMaterialized(job)) {
                sLog.info("[AI-CONSUMER] Job {} was already materialized. Skipping duplicate trip creation.", event.jobId());
                return;
            }

            job.setStatus(OptimizationJobStatus.COMPLETED);
            String materializedPayload = materializeRecommendedTrips(event, targetUserEmail);
            job.setRecommendationsPayload(materializedPayload);
            optimizationJobRepository.save(job);

            // Gửi thông báo thành công cho người dùng qua Notify Processor
            publishNotification(
                    event.merchantId(),
                    targetUserEmail,
                    "Tối ưu hóa lịch trình thành công",
                    "Lịch trình chuyến đi thông minh đã được khởi tạo. Bạn có thể mở chi tiết thông báo để xem danh sách chuyến đã tạo.",
                    "AI_OPTIMIZATION_COMPLETED",
                    event.jobId()
            );

        } catch (Exception e) {
            sLog.error("[AI-CONSUMER] Error processing trip creation for Job: {} {}", event.jobId(), ExceptionUtils.getStackTrace(e));
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

    @SuppressWarnings("unchecked")
    private String materializeRecommendedTrips(AiOptimizationCompletedEvent event, String targetUserEmail) throws Exception {
        sLog.info("[AI-CONSUMER] Starting to create trip batches from recommendations for Job: {}", event.jobId());

        Map<String, Object> payload = objectMapper.readValue(event.recommendationsPayload(), new TypeReference<>() {});
        List<Map<String, Object>> schedules = (List<Map<String, Object>>) payload.getOrDefault("schedules", List.of());

        List<TripMaterializationPlan> plans = new ArrayList<>();
        List<CreateTripBatchCommand.TripBatchCommandData> tripsToCreate = new ArrayList<>();

        for (Map<String, Object> schedule : schedules) {
            String date = asString(schedule.get("date"));
            List<Map<String, Object>> optimalTrips = (List<Map<String, Object>>) schedule.getOrDefault("optimal_trips", List.of());
            for (Map<String, Object> trip : optimalTrips) {
                String rawDepartureTime = asString(trip.get("departure_time"));
                Map<String, Object> vehicle = (Map<String, Object>) trip.getOrDefault("assigned_vehicle", Map.of());
                Map<String, Object> driver = (Map<String, Object>) trip.getOrDefault("assigned_driver", Map.of());
                String vehicleId = asString(vehicle.get("vehicle_id"));
                String driverId = asString(driver.get("driver_id"));

                OffsetDateTime departureTime = toDepartureTime(date, rawDepartureTime);
                plans.add(new TripMaterializationPlan(schedule, trip, vehicleId, driverId));
                tripsToCreate.add(CreateTripBatchCommand.TripBatchCommandData.builder()
                        .departureTime(departureTime)
                        .rawDepartureDate(toRawDepartureDate(date))
                        .rawDepartureTime(rawDepartureTime)
                        .build());
            }
        }

        if (tripsToCreate.isEmpty()) {
            payload.put("createdTrips", List.of());
            payload.put("assignmentSummary", Map.of("successCount", 0, "failedCount", 0));
            return objectMapper.writeValueAsString(payload);
        }

        RequestContext context = RequestContext.builder()
                .requestId(UUID.randomUUID().toString())
                .requestDateTime(OffsetDateTime.now().toString())
                .channel("AI_OPTIMIZATION")
                .merchantId(event.merchantId())
                .userEmail(targetUserEmail)
                .build();

        CreateTripBatchResult createResult = merchantTripService.createTripBatch(CreateTripBatchCommand.builder()
                .context(context)
                .merchantId(event.merchantId())
                .routeId(event.routeId())
                .trips(tripsToCreate)
                .build());

        List<String> createdTripIds = createResult.tripIds();
        List<AssignRouteBatchCommand.AssignRouteBatchItem> assignments = new ArrayList<>();
        List<Map<String, Object>> createdTrips = new ArrayList<>();

        for (int i = 0; i < plans.size(); i++) {
            TripMaterializationPlan plan = plans.get(i);
            String tripId = createdTripIds.get(i);
            plan.trip().put("createdTripId", tripId);
            plan.trip().put("tripId", tripId);

            Map<String, Object> createdTrip = new LinkedHashMap<>();
            createdTrip.put("tripId", tripId);
            createdTrip.put("date", plan.schedule().get("date"));
            createdTrip.put("departureTime", plan.trip().get("departure_time"));
            createdTrip.put("vehicleId", plan.vehicleId());
            createdTrip.put("driverId", plan.driverId());
            createdTrips.add(createdTrip);

            if (hasText(plan.vehicleId()) && hasText(plan.driverId())) {
                assignments.add(AssignRouteBatchCommand.AssignRouteBatchItem.builder()
                        .tripId(tripId)
                        .vehicleId(plan.vehicleId())
                        .driverId(plan.driverId())
                        .build());
            }
        }

        AssignRouteBatchResult assignResult = null;
        if (!assignments.isEmpty()) {
            assignResult = merchantTripService.assignRouteBatch(AssignRouteBatchCommand.builder()
                    .context(context)
                    .merchantId(event.merchantId())
                    .creator(targetUserEmail)
                    .assignments(assignments)
                    .build());
        }

        payload.put("createdTrips", createdTrips);
        payload.put("assignmentSummary", Map.of(
                "successCount", assignResult == null ? 0 : assignResult.successCount(),
                "failedCount", assignResult == null ? 0 : assignResult.failedCount(),
                "failedItems", assignResult == null ? List.of() : assignResult.failedItems()
        ));

        return objectMapper.writeValueAsString(payload);
    }

    private boolean isAlreadyMaterialized(OptimizationJobEntity job) {
        if (!OptimizationJobStatus.COMPLETED.equals(job.getStatus()) || job.getRecommendationsPayload() == null) {
            return false;
        }
        return job.getRecommendationsPayload().contains("\"createdTrips\"");
    }

    private OffsetDateTime toDepartureTime(String date, String time) {
        return LocalDate.parse(date, AI_DATE_FORMATTER)
                .atTime(LocalTime.parse(time))
                .atZone(DEFAULT_ZONE)
                .toOffsetDateTime();
    }

    private String toRawDepartureDate(String date) {
        return LocalDate.parse(date, AI_DATE_FORMATTER).format(RAW_DEPARTURE_DATE_FORMATTER);
    }

    private String asString(Object value) {
        return value == null ? null : value.toString();
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private record TripMaterializationPlan(
            Map<String, Object> schedule,
            Map<String, Object> trip,
            String vehicleId,
            String driverId
    ) {
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
