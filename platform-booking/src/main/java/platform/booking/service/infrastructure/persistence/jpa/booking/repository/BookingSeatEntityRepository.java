package platform.booking.service.infrastructure.persistence.jpa.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import platform.core.common.service.domain.booking.BookingSeatStatus;
import platform.booking.service.infrastructure.persistence.jpa.booking.entity.BookingSeatEntity;

import java.util.List;
import java.util.Optional;

public interface BookingSeatEntityRepository extends JpaRepository<BookingSeatEntity, String> {
    List<BookingSeatEntity> findAllByBookingId(String bookingId);

    Optional<BookingSeatEntity> findFirstByBookingId(String bookingId);

    List<BookingSeatEntity> findByBookingIdAndStatus(String bookingId, BookingSeatStatus status);
}
