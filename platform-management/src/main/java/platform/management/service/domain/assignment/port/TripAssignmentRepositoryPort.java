package platform.management.service.domain.assignment.port;

import platform.management.service.domain.assignment.TripAssignmentStatus;
import platform.management.service.domain.assignment.model.TripAssignmentRecord;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface TripAssignmentRepositoryPort {
    boolean existsActiveByTripId(String tripId);

    boolean existsActiveByTripId(String tripId, String merchantId);


    Optional<TripAssignmentRecord> findByTripIdAndStatus(String tripId, TripAssignmentStatus status);

    Optional<TripAssignmentRecord> findActiveByTripId(String tripId);

    Optional<TripAssignmentRecord> findActiveByTripId(String tripId, String merchantId);

    Map<String, TripAssignmentRecord> findLatestActiveByTripIds(List<String> tripIds);

    Map<String, TripAssignmentRecord> findLatestActiveByTripIds(List<String> tripIds, String merchantId);

    List<TripAssignmentRecord> findByMerchantId(String merchantId);

    void save(TripAssignmentRecord assignment);
}
