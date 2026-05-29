package platform.core.common.service.application.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import platform.core.common.service.domain.outbox.OutBoxEventStatus;
import platform.core.common.service.domain.outbox.model.OutBoxEvent;
import platform.core.common.service.domain.outbox.port.OutBoxEventRepositoryPort;
import platform.core.common.service.application.service.OutBoxService;
import vn.com.go.routex.identity.security.log.SystemLog;
import platform.core.common.service.api.BaseRequest;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OutBoxServiceImpl implements OutBoxService {
    private final OutBoxEventRepositoryPort outboxEventRepositoryPort;
    private final SystemLog sLog = SystemLog.getLogger(this.getClass());

    @Override
    @Transactional
    public void generateEvent(String aggregateId, String topic, String eventName, String eventKey, Object payload, BaseRequest context) {
        OutBoxEvent outboxEvent = OutBoxEvent.builder()
                .id(UUID.randomUUID().toString())
                .aggregateId(aggregateId)
                .topic(topic)
                .eventType(eventName)
                .eventKey(eventKey)
                .payload(Map.of("data", payload))
                .header(Map.of("context", context))
                .status(OutBoxEventStatus.PENDING)
                .retryCount(0)
                .availableAt(OffsetDateTime.now())
                .processedAt(null)
                .build();

        outboxEventRepositoryPort.save(outboxEvent);
        sLog.info("[OUTBOX-EVENT] Outbox event generated and queued: AggregateId={} EventId={}",
                aggregateId, outboxEvent.getId());
    }
}
