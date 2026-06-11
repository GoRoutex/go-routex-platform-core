package platform.management.service.domain.assignment.port;


import platform.merchant.service.domain.assignment.TripAssignmentStatus;
import platform.merchant.service.domain.assignment.model.TripAssignmentRecord;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface TripAssignmentRepositoryPort {
    Optional<TripAssignmentRecord> findByTripIdAndStatus(String tripId, TripAssignmentStatus status);

    Map<String, TripAssignmentRecord> findLatestActiveByTripIds(List<String> tripIds);

    void save(TripAssignmentRecord assignment);
}
