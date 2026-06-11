package platform.merchant.service.infrastructure.persistence.adapter.trip;

import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import platform.core.common.service.application.command.common.PagedResult;
import platform.core.common.service.domain.trip.TripStatus;
import platform.merchant.service.domain.trip.model.TripAggregate;
import platform.merchant.service.domain.trip.port.TripAggregateRepositoryPort;
import platform.merchant.service.infrastructure.persistence.jpa.trip.entity.TripEntity;
import platform.merchant.service.infrastructure.persistence.jpa.trip.repository.TripEntityRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TripAggregateRepositoryAdapter implements TripAggregateRepositoryPort {

    private final TripEntityRepository tripEntityRepository;
    private final TripAggregatePersistenceMapper tripAggregatePersistenceMapper;

    @Override
    public String generateTripCode(String originCode, String destinationCode) {
        return tripEntityRepository.generateTripCode(originCode, destinationCode);
    }

    @Override
    public boolean existsByRouteId(String routeId, String merchantId) {
        return tripEntityRepository.existsByRouteIdAndMerchantId(routeId, merchantId);
    }

    @Override
    public Optional<TripAggregate> findById(String tripId) {
        return tripEntityRepository.findById(tripId)
                .map(tripAggregatePersistenceMapper::toDomain);
    }

    @Override
    public Optional<TripAggregate> findById(String tripId, String merchantId) {
        return tripEntityRepository.findByIdAndMerchantId(tripId, merchantId)
                .map(tripAggregatePersistenceMapper::toDomain);
    }

    @Override
    public Optional<TripAggregate> findByRouteId(String routeId, String merchantId) {
        return tripEntityRepository.findByRouteIdAndMerchantId(routeId, merchantId)
                .map(tripAggregatePersistenceMapper::toDomain);
    }

    @Override
    public void save(TripAggregate aggregate) {
        tripEntityRepository.save(tripAggregatePersistenceMapper.toEntity(aggregate));
    }

    @Override
    public void saveAll(java.util.List<TripAggregate> aggregates) {
       List<TripEntity> entities = aggregates.stream()
                .map(tripAggregatePersistenceMapper::toEntity)
                .toList();
        tripEntityRepository.saveAll(entities);
    }

    @Override
    public PagedResult<TripAggregate> fetch(String merchantId, TripStatus status, String rawDepartureDate, int pageNumber, int pageSize) {
        Page<TripEntity> page = tripEntityRepository.fetchAllTrips(merchantId, status, rawDepartureDate, PageRequest.of(pageNumber, pageSize));
        return toPagedResult(page);
    }

    @Override
    public List<TripAggregate> findByIds(List<String> tripIds, String merchantId) {
        return tripEntityRepository.findAllByIdInAndMerchantId(tripIds, merchantId)
                .stream()
                .map(tripAggregatePersistenceMapper::toDomain)
                .toList();
    }

    @Override
    public Page<TripAggregate> findAll(Specification<TripAggregate> specification, Pageable pageable) {
        Specification<TripEntity> entitySpec = (root, query, cb) -> specification.toPredicate((Root) root, query, cb);
        return tripEntityRepository.findAll(entitySpec, pageable).map(tripAggregatePersistenceMapper::toDomain);
    }

    @Override
    public Page<TripAggregate> findAllByFilter(OffsetDateTime from, OffsetDateTime to, Pageable pageable) {
        return tripEntityRepository.findAllBy(from, to, pageable).map(tripAggregatePersistenceMapper::toDomain);
    }

    private PagedResult<TripAggregate> toPagedResult(Page<TripEntity> page) {
        return PagedResult.<TripAggregate>builder()
                .items(page.getContent().stream()
                        .map(tripAggregatePersistenceMapper::toDomain)
                        .toList())
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
    }
}
