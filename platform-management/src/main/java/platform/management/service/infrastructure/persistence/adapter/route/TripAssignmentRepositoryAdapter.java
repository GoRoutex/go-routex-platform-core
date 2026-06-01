package platform.management.service.infrastructure.persistence.adapter.route;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import platform.management.service.domain.assignment.port.TripAssignmentRepositoryPort;
import platform.merchant.service.domain.assignment.TripAssignmentStatus;
import platform.merchant.service.domain.assignment.model.TripAssignmentRecord;
import platform.merchant.service.infrastructure.persistence.jpa.assignment.entity.TripAssignmentEntity;
import platform.merchant.service.infrastructure.persistence.jpa.assignment.repository.TripAssignmentEntityRepository;
import vn.com.go.routex.identity.security.log.SystemLog;

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

    private final SystemLog sLog = SystemLog.getLogger(this.getClass());

    @Override
    public boolean existsActiveByTripId(String tripId) {
        return tripAssignmentEntityRepository.existsByTripId(tripId);
    }

    @Override
    public boolean existsActiveByTripId(String tripId, String merchantId) {
        return tripAssignmentEntityRepository.existsByTripIdAndMerchantId(tripId, merchantId);
    }

    @Override
    public Optional<TripAssignmentRecord> findByTripIdAndStatus(String tripId, TripAssignmentStatus status) {
        Optional<TripAssignmentEntity> entityOptional = tripAssignmentEntityRepository.findByTripIdAndStatus(tripId, TripAssignmentStatus.PENDING_ASSIGNMENT);

        if(entityOptional.isPresent()) {
            TripAssignmentEntity tripAssignmentEntity = entityOptional.get();
            return Optional.ofNullable(routePersistenceMapper.toAssignmentRecord(tripAssignmentEntity));
        } else {
            sLog.info("not found");
        }

        return null;
    }

    @Override
    public Optional<TripAssignmentRecord> findActiveByTripId(String tripId) {
        return tripAssignmentEntityRepository
                .findFirstByTripIdAndStatusAndUnAssignedAtIsNullOrderByAssignedAtDesc(tripId, TripAssignmentStatus.ASSIGNED)
                .map(routePersistenceMapper::toAssignmentRecord);
    }

    @Override
    public Optional<TripAssignmentRecord> findActiveByTripId(String tripId, String merchantId) {
        return tripAssignmentEntityRepository
                .findFirstByTripIdAndMerchantIdAndStatusAndUnAssignedAtIsNullOrderByAssignedAtDesc(
                        tripId,
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

        sLog.info("Assignment: {}", assignments);
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
}
