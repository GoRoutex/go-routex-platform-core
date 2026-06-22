package platform.core.common.service.application.service;

import java.time.OffsetDateTime;

public interface EntityPartitionService {

    void ensureTripPartition(OffsetDateTime departureTime);
    void ensureTicketPartition(OffsetDateTime issuedAt);
}
