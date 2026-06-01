package platform.management.service.infrastructure.persistence.adapter.trip;

import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import platform.core.common.service.application.command.common.PagedResult;
import platform.management.service.domain.trip.model.TripAggregate;
import platform.management.service.domain.trip.port.TripAggregateRepositoryPort;
import platform.merchant.service.infrastructure.persistence.jpa.trip.entity.TripEntity;
import platform.merchant.service.infrastructure.persistence.jpa.trip.repository.TripEntityRepository;

import java.util.Optional;
import java.util.stream.Collectors;

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
    public Optional<TripAggregate> findById(String tripId, String merchantId) {
        return tripEntityRepository.findByIdAndMerchantId(tripId, merchantId)
                .map(tripAggregatePersistenceMapper::toDomain);
    }

    @Override
    public Optional<TripAggregate> findById(String tripId) {
        return tripEntityRepository.findById(tripId)
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
    public PagedResult<TripAggregate> fetch(String merchantId, int pageNumber, int pageSize) {
        Page<TripEntity> page = tripEntityRepository.findByMerchantId(merchantId, PageRequest.of(pageNumber, pageSize));
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

    @Override
    public PagedResult<TripAggregate> fetch(Specification<TripAggregate> spec, Pageable pageable) {
        Specification<TripEntity> entitySpec = (root, query, cb) -> spec.toPredicate((Root) root, query, cb);
        Page<TripEntity> entityPage = tripEntityRepository.findAll(entitySpec, pageable);
        return new PagedResult<>(
                entityPage.getContent().stream().map(tripAggregatePersistenceMapper::toDomain).collect(Collectors.toList()),
                entityPage.getNumber(),
                entityPage.getSize(),
                entityPage.getTotalElements(),
                entityPage.getTotalPages()
        );
    }

    @Override
    public Page<TripAggregate> findAll(Specification<TripAggregate> specification, Pageable pageable) {
        Specification<TripEntity> entitySpec = (root, query, cb) -> specification.toPredicate((Root) root, query, cb);
        return tripEntityRepository.findAll(entitySpec, pageable).map(tripAggregatePersistenceMapper::toDomain);
    }
}
