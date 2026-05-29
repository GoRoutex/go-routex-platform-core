package platform.core.common.service.domain.booking.port;


import platform.core.common.service.domain.booking.model.BookingSeat;

import java.util.List;
import java.util.Optional;

public interface BookingSeatRepositoryPort {
    List<BookingSeat> saveAll(List<BookingSeat> bookingSeats);

    BookingSeat save(BookingSeat bookingSeat);

    List<BookingSeat> findAllByBookingId(String bookingId);

    List<BookingSeat> findByBookingId(String bookingId);

    Optional<BookingSeat> findOneByBookingId(String bookingId);

    List<BookingSeat> findByBookingIdAndStatus(String bookingId, platform.core.common.service.domain.booking.BookingSeatStatus status);
}
