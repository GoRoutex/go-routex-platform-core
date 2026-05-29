package platform.merchant.service.infrastructure.persistence.jpa.seat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import platform.merchant.service.infrastructure.persistence.jpa.seat.entity.TripSeatEntity;
import platform.merchant.service.infrastructure.persistence.jpa.seat.projection.TripSeatAvailabilityProjection;

import java.util.List;
import java.util.Optional;

@Repository
public interface TripSeatEntityRepository extends JpaRepository<TripSeatEntity, String> {

    boolean existsByTripId(String tripId);

    @Query(value = """
            SELECT rs.TRIP_ID AS tripId, COUNT(*) AS availableSeat
            FROM TRIP_SEAT rs
            WHERE rs.TRIP_ID IN :tripIds
              AND rs.STATUS = :status
            GROUP BY rs.TRIP_ID
            """, nativeQuery = true)
    List<TripSeatAvailabilityProjection> countAvailableSeatsByTripIdAndStatus(@Param("tripIds") List<String> tripIds,
                                                                               @Param("status") String status);

    List<TripSeatEntity> findAllByTripIdOrderBySeatNoAsc(String tripId);

    List<TripSeatEntity> findAllByTripIdAndSeatNoIn(String tripId, List<String> seatNos);

    Optional<TripSeatEntity> findByTripIdAndSeatNo(String tripId, String seatNo);
}
