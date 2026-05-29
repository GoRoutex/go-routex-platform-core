package platform.merchant.service.domain.assignment.port;

import platform.merchant.service.domain.assignment.model.TripAssignmentRecord;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface TripAssignmentRepositoryPort {
    boolean existsActiveByTripId(String tripId);

    boolean existsActiveByTripId(String tripId, String merchantId);

    Optional<TripAssignmentRecord> findByTripIdAndMerchantId(String tripId, String merchantId);

    List<TripAssignmentRecord> findByTripIdAndMerchantId(List<String> tripIds, String merchantId);

    Optional<TripAssignmentRecord> findActiveByTripId(String tripId);

    Optional<TripAssignmentRecord> findActiveByTripId(String tripId, String merchantId);

    Map<String, TripAssignmentRecord> findLatestActiveByTripIds(List<String> tripIds);

    Map<String, TripAssignmentRecord> findLatestActiveByTripIds(List<String> tripIds, String merchantId);

    List<TripAssignmentRecord> findByMerchantId(String merchantId);

    void save(TripAssignmentRecord assignment);

    void saveAll(List<TripAssignmentRecord> assignments);

    List<TripAssignmentRecord> findActiveByDriver(String driverId);

    List<TripAssignmentRecord> findActiveByVehicle(String vehicleId);
}
