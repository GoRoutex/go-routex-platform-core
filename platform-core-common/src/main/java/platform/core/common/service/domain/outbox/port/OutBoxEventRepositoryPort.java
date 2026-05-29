package platform.core.common.service.domain.outbox.port;


import platform.core.common.service.domain.outbox.model.OutBoxEvent;

import java.time.OffsetDateTime;
import java.util.List;

public interface OutBoxEventRepositoryPort {
    void save(OutBoxEvent outboxEvent);

    void markAsProcessed(List<String> processedIds, OffsetDateTime now);
    void markAsFailed(List<String> failedIds, OffsetDateTime now);
    List<OutBoxEvent> lockPendingBatch(int i);
}
