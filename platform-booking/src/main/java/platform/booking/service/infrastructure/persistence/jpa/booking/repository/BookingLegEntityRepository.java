package platform.booking.service.infrastructure.persistence.jpa.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import platform.booking.service.infrastructure.persistence.jpa.booking.entity.BookingLegEntity;

import java.util.List;

@Repository
public interface BookingLegEntityRepository extends JpaRepository<BookingLegEntity, String> {
    List<BookingLegEntity> findByBookingId(String bookingId);
}
