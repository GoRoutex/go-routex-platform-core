package platform.booking.service.infrastructure.persistence.jpa.booking.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import platform.core.common.service.domain.booking.BookingStatus;
import platform.booking.service.infrastructure.persistence.jpa.booking.entity.BookingEntity;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingEntityRepository extends JpaRepository<BookingEntity, String> {

    Optional<BookingEntity> findByIdAndMerchantId(String id, String merchantId);

    Optional<BookingEntity> findByTripId(String tripId);

    Optional<BookingEntity> findByBookingCode(String bookingCode);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select b
            from Booking_BookingEntity b
            where b.bookingCode = :bookingCode
            """)
    Optional<BookingEntity> findByBookingCodeForUpdate(@Param("bookingCode") String bookingCode);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select b
            from Booking_BookingEntity b
            where b.status = :status
              and b.holdUntil <= :holdUntil
            order by b.holdUntil asc
            """)
    List<BookingEntity> findExpiredPendingPaymentBookingsForUpdate(@Param("holdUntil") OffsetDateTime holdUntil,
                                                                   Pageable pageable,
                                                                   @Param("status") BookingStatus status);

    @Query(value = """
            SELECT generate_booking_code()
            """, nativeQuery = true)
    String generateBookingCode();
}
