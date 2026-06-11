package platform.merchant.service.domain.trip.port;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import platform.core.common.service.application.command.common.PagedResult;
import platform.core.common.service.domain.trip.TripStatus;
import platform.merchant.service.domain.trip.model.TripAggregate;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface TripAggregateRepositoryPort {

    String generateTripCode(String originCode, String destinationCode);

    boolean existsByRouteId(String routeId, String merchantId);

    Optional<TripAggregate> findById(String tripId);

    Optional<TripAggregate> findById(String tripId, String merchantId);

    Optional<TripAggregate> findByRouteId(String routeId, String merchantId);

    void save(TripAggregate aggregate);

    void saveAll(List<TripAggregate> aggregates);

    PagedResult<TripAggregate> fetch(String merchantId, TripStatus status, String rawDepartureDate, int pageNumber, int pageSize);

    List<TripAggregate> findByIds(List<String> tripIds, String merchantId);

    Page<TripAggregate> findAll(Specification<TripAggregate> specification, Pageable pageable);

    Page<TripAggregate> findAllByFilter(OffsetDateTime from, OffsetDateTime to, Pageable pageable);
}
