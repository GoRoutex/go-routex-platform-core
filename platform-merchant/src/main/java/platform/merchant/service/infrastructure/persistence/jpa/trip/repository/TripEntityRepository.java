package platform.merchant.service.infrastructure.persistence.jpa.trip.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import platform.core.common.service.domain.trip.TripStatus;
import platform.merchant.service.infrastructure.persistence.jpa.trip.entity.TripEntity;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TripEntityRepository extends JpaRepository<TripEntity, String>, JpaSpecificationExecutor<TripEntity> {

    boolean existsByRouteIdAndMerchantId(String routeId, String merchantId);

    Optional<TripEntity> findByIdAndMerchantId(String id, String merchantId);

    Optional<TripEntity> findByRouteIdAndMerchantId(String routeId, String merchantId);

    Page<TripEntity> findByMerchantId(String merchantId, Pageable pageable);

    @Query(value = """
            SELECT generate_trip_code(:origin, :destination)
            """, nativeQuery = true)
    String generateTripCode(@Param("origin") String origin,
                             @Param("destination") String destination);


    @Query(value = """
            SELECT t from TripEntity t
                        WHERE t.departureTime >= :from
                        AND t.departureTime <= :to
                        AND t.status = 'ASSIGNED'
            """)
    Page<TripEntity> findAllBy(
            @Param("from") OffsetDateTime from,
            @Param("to") OffsetDateTime to,
            Pageable pageable);

    @Query(value = """
        SELECT t FROM TripEntity t
                WHERE t.merchantId = :merchantId
                AND (:status IS NULL OR t.status = :status)
                AND (:rawDepartureDate IS NULL OR t.rawDepartureDate = :rawDepartureDate)
                AND t.departureTime >= :fromDepartureTime
                AND t.departureTime < :toDepartureTime
                ORDER BY t.departureTime ASC, t.id ASC
        """)
    Page<TripEntity> fetchAllTrips(
            @Param("merchantId") String merchantId,
            @Param("status") TripStatus status,
            @Param("rawDepartureDate") String rawDepartureDate,
            @Param("fromDepartureTime") OffsetDateTime fromDepartureTime,
            @Param("toDepartureTime") OffsetDateTime toDepartureTime,
            Pageable pageable);

    List<TripEntity> findAllByIdInAndMerchantId(List<String> ids, String merchantId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT t FROM TripEntity t
            WHERE t.status = 'ASSIGNED'
            AND t.departureTime < :cutoff
            ORDER BY t.departureTime ASC, t.id ASC
            """)
    List<TripEntity> findAssignedTripsBeforeForUpdate(@Param("cutoff") OffsetDateTime cutoff, Pageable pageable);

}
