package platform.booking.service.application.services;

import platform.booking.service.application.command.booking.CreateBookingCommand;
import platform.booking.service.domain.tripcontext.model.TripBookingContext;
import platform.core.common.service.domain.booking.model.Booking;
import platform.core.common.service.domain.seat.model.TripSeat;

import java.util.List;

public interface BookingService {

    Booking createBooking(CreateBookingCommand command, TripBookingContext tripContext, List<TripSeat> tripSeatList);

}
