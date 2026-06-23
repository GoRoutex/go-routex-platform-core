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
    public Optional<TripAssignmentRecord> findByTripIdAndStatus(String tripId, TripAssignmentStatus status) {
        Optional<TripAssignmentEntity> entityOptional = tripAssignmentEntityRepository.findByTripIdAndStatus(tripId, status);

        if(entityOptional.isPresent()) {
            TripAssignmentEntity tripAssignmentEntity = entityOptional.get();
            return Optional.ofNullable(routePersistenceMapper.toAssignmentRecord(tripAssignmentEntity));
        } else {
            sLog.info("not found");
        }

        return Optional.empty();
    }

    @Override
    public Map<String, TripAssignmentRecord> findLatestActiveByTripIds(List<String> tripIds) {
        List<TripAssignmentEntity> assignments = tripAssignmentEntityRepository.findActiveByTripIdsNative(tripIds, TripAssignmentStatus.ASSIGNED.name());
        return toAssignmentMap(assignments);
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
