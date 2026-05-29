package platform.management.service.application.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import vn.com.go.routex.identity.security.log.SystemLog;
import platform.management.service.application.services.OutBoxRelayService;
import platform.management.service.domain.outbox.model.OutBoxEvent;
import platform.management.service.domain.outbox.port.OutBoxEventRepositoryPort;
import platform.management.service.infrastructure.kafka.config.KafkaEventPublisher;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
public class OutBoxRelayServiceImpl implements OutBoxRelayService {
    private final OutBoxEventRepositoryPort outboxEventRepositoryPort;
    private final KafkaEventPublisher kafkaEventPublisher;
    private final SystemLog sLog = SystemLog.getLogger(this.getClass());

    @Value("${locking.batch}")
    private int lockingBatch;

    @Override
    @Transactional
    public void pollingEvent() {
        List<OutBoxEvent> outBoxEventList = outboxEventRepositoryPort.lockPendingBatch(lockingBatch);

        if (outBoxEventList.isEmpty()) {
            return;
        }

        sLog.info("[OUTBOX-EVENT] Relaying {} outbox events to Kafka", outBoxEventList.size());

        List<String> processedIds = new ArrayList<>();
        List<String> failedIds = new ArrayList<>();
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (OutBoxEvent event : outBoxEventList) {
            CompletableFuture<Void> future = kafkaEventPublisher.publishAsync(event)
                    .thenRun(() -> {
                        sLog.info("[OUTBOX-EVENT] Event published successfully: eventId={} eventType", event.getId(), event.getEventType());
                        synchronized (processedIds) {
                            processedIds.add(event.getId());
                        }
                    })
                    .exceptionally(ex -> {
                        sLog.error("Failed to publish event: eventId={}, eventType={}",
                                event.getId(), event.getEventType(), ex);
                        synchronized (failedIds) {
                            failedIds.add(event.getId());
                        }
                        return null;
                    });

            futures.add(future);
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        OffsetDateTime now =  OffsetDateTime.now();

        if (!processedIds.isEmpty()) {
            outboxEventRepositoryPort.markAsProcessed(processedIds, now);
        }

        if (!failedIds.isEmpty()) {
            outboxEventRepositoryPort.markAsFailed(failedIds, now);
        }

        sLog.info("[OUTBOX-EVENT] Relay completed. processed={}, failed={}", processedIds.size(), failedIds.size());
    }
}
