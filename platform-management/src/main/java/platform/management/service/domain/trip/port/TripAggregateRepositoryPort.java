package platform.management.service.domain.trip.port;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import platform.core.common.service.application.command.common.PagedResult;
import platform.management.service.domain.trip.model.TripAggregate;

import java.util.Optional;

public interface TripAggregateRepositoryPort {

    String generateTripCode(String originCode, String destinationCode);

    boolean existsByRouteId(String routeId, String merchantId);

    Optional<TripAggregate> findById(String tripId, String merchantId);

    Optional<TripAggregate> findById(String tripId);

    Optional<TripAggregate> findByRouteId(String routeId, String merchantId);

    void save(TripAggregate aggregate);

    PagedResult<TripAggregate> fetch(String merchantId, int pageNumber, int pageSize);

    PagedResult<TripAggregate> fetch(Specification<TripAggregate> spec, Pageable pageable);

    Page<TripAggregate> findAll(Specification<TripAggregate> specification, Pageable pageable);
}
