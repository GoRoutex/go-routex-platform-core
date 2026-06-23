package platform.core.common.service.domain.booking.port;

import platform.core.common.service.domain.booking.model.BookingLeg;

import java.util.List;

public interface BookingLegRepositoryPort {
    BookingLeg save(BookingLeg bookingLeg);
    List<BookingLeg> saveAll(List<BookingLeg> bookingLegs);
    List<BookingLeg> findAllByBookingId(String bookingId);
}
