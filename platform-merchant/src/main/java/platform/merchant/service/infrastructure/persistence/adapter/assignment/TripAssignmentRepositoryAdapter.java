package platform.merchant.service.infrastructure.persistence.adapter.assignment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import platform.merchant.service.domain.assignment.TripAssignmentStatus;
import platform.merchant.service.domain.assignment.model.TripAssignmentRecord;
import platform.merchant.service.domain.assignment.port.TripAssignmentRepositoryPort;
import platform.merchant.service.infrastructure.persistence.adapter.route.RoutePersistenceMapper;
import platform.merchant.service.infrastructure.persistence.jpa.assignment.entity.TripAssignmentEntity;
import platform.merchant.service.infrastructure.persistence.jpa.assignment.repository.TripAssignmentEntityRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TripAssignmentRepositoryAdapter implements TripAssignmentRepositoryPort {

    private final TripAssignmentEntityRepository tripAssignmentEntityRepository;
    private final RoutePersistenceMapper routePersistenceMapper;
    private static final List<TripAssignmentStatus> ACTIVE_ASSIGNMENT_STATUSES = List.of(
            TripAssignmentStatus.PENDING_ASSIGNMENT,
            TripAssignmentStatus.ASSIGNED
    );

    @Override
    public boolean existsActiveByTripId(String tripId) {
        return tripAssignmentEntityRepository.existsByTripId(tripId);
    }

    @Override
    public boolean existsActiveByTripId(String tripId, String merchantId) {
        return tripAssignmentEntityRepository.existsByTripIdAndMerchantIdAndStatusIn(
                tripId,
                merchantId,
                ACTIVE_ASSIGNMENT_STATUSES
        );
    }


    @Override
    public Optional<TripAssignmentRecord> findByTripIdAndMerchantId(String tripId, String merchantId) {
        return tripAssignmentEntityRepository.findByTripIdAndMerchantId(tripId, merchantId)
                .map(routePersistenceMapper::toAssignmentRecord);
    }

    @Override
    public List<TripAssignmentRecord> findByTripIdAndMerchantId(List<String> tripIds, String merchantId) {
        return tripAssignmentEntityRepository.findByTripIdInAndMerchantId(tripIds, merchantId)
                .stream()
                .map(routePersistenceMapper::toAssignmentRecord).toList();
    }

    @Override
    public Optional<TripAssignmentRecord> findActiveByTripId(String tripId) {
        return tripAssignmentEntityRepository
                .findFirstByTripIdAndStatusAndUnAssignedAtIsNullOrderByAssignedAtDesc(tripId, TripAssignmentStatus.ASSIGNED)
                .map(routePersistenceMapper::toAssignmentRecord);
    }

    @Override
    public Optional<TripAssignmentRecord> findActiveByTripId(String TripId, String merchantId) {
        return tripAssignmentEntityRepository
                .findFirstByTripIdAndMerchantIdAndStatusAndUnAssignedAtIsNullOrderByAssignedAtDesc(
                        TripId,
                        merchantId,
                        TripAssignmentStatus.ASSIGNED
                )
                .map(routePersistenceMapper::toAssignmentRecord);
    }

    @Override
    public Map<String, TripAssignmentRecord> findLatestActiveByTripIds(List<String> tripIds) {
        List<TripAssignmentEntity> assignments = tripAssignmentEntityRepository.findActiveByTripIdsNative(tripIds, TripAssignmentStatus.ASSIGNED.name());
        return toAssignmentMap(assignments);
    }

    @Override
    public Map<String, TripAssignmentRecord> findLatestActiveByTripIds(List<String> tripIds, String merchantId) {
        List<TripAssignmentEntity> assignments = tripAssignmentEntityRepository.findActiveByTripIdsAndMerchantIdNative(
                tripIds,
                merchantId,
                TripAssignmentStatus.ASSIGNED.name()
        );
        return toAssignmentMap(assignments);
    }

    @Override
    public List<TripAssignmentRecord> findByMerchantId(String merchantId) {
        return tripAssignmentEntityRepository.findByMerchantId(merchantId).stream()
                .map(routePersistenceMapper::toAssignmentRecord)
                .toList();
    }

    private Map<String, TripAssignmentRecord> toAssignmentMap(List<TripAssignmentEntity> assignments) {
        return assignments.stream()
                .map(routePersistenceMapper::toAssignmentRecord)
                .collect(Collectors.toMap(
                        TripAssignmentRecord::getTripId,
                        Function.identity(),
                        BinaryOperator.maxBy(Comparator.comparing(TripAssignmentRecord::getAssignedAt))
                ));
    }

    @Override
    public void save(TripAssignmentRecord assignment) {
        tripAssignmentEntityRepository.save(routePersistenceMapper.toEntity(assignment));
    }

    @Override
    public void saveAll(List<TripAssignmentRecord> assignments) {
        List<TripAssignmentEntity> entities = assignments.stream()
                .map(routePersistenceMapper::toEntity)
                .toList();
        tripAssignmentEntityRepository.saveAll(entities);
    }

    @Override
    public List<TripAssignmentRecord> findActiveByDriver(String driverId) {
        return tripAssignmentEntityRepository
                .findByDriverIdAndStatusAndUnAssignedAtIsNull(driverId, TripAssignmentStatus.ASSIGNED)
                .stream()
                .map(routePersistenceMapper::toAssignmentRecord)
                .toList();
    }

    @Override
    public List<TripAssignmentRecord> findActiveByVehicle(String vehicleId) {
        return tripAssignmentEntityRepository
                .findByVehicleIdAndStatusAndUnAssignedAtIsNull(vehicleId, TripAssignmentStatus.ASSIGNED)
                .stream()
                .map(routePersistenceMapper::toAssignmentRecord)
                .toList();
    }

    @Override
    public Optional<TripAssignmentRecord> findByTripId(String id) {
        return tripAssignmentEntityRepository.findFirstByTripIdOrderByAssignedAtDesc(id)
                .map(routePersistenceMapper::toAssignmentRecord);
    }
}
