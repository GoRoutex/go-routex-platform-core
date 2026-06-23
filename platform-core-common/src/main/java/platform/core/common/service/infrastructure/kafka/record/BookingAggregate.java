package platform.core.common.service.infrastructure.kafka.record;


import platform.core.common.service.domain.booking.model.Booking;
import platform.core.common.service.domain.booking.model.BookingLeg;
import platform.core.common.service.domain.booking.model.BookingSeat;
import platform.core.common.service.domain.payment.model.PaymentAggregate;
import platform.core.common.service.domain.seat.model.TripSeat;

import java.util.List;

public record BookingAggregate(
        Booking booking,
        List<BookingLeg> bookingLegs,
        List<BookingSeat> bookingSeats,
        List<TripSeat> tripSeats,
        PaymentAggregate paymentAggregate
) {
}
