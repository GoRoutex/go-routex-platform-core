package platform.merchant.service.infrastructure.persistence.jpa.trip.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import platform.core.common.service.domain.trip.TripStatus;
import platform.merchant.service.infrastructure.persistence.jpa.trip.entity.TripEntity;

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
        SELECT t FROM TripEntity t
                WHERE t.merchantId = :merchantId
                AND (:status IS NULL OR t.status = :status)
                AND (:rawDepartureDate IS NULL OR t.rawDepartureDate= :rawDepartureDate)
        """)
    Page<TripEntity> fetchAllTrips(
            @Param("merchantId") String merchantId,
            @Param("status") TripStatus status,
            @Param("rawDepartureDate") String rawDepartureDate, Pageable pageable);

    List<TripEntity> findAllByIdInAndMerchantId(List<String> ids, String merchantId);

}
