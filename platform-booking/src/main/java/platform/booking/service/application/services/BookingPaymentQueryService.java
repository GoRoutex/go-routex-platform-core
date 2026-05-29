package platform.booking.service.application.services;

import platform.core.common.service.common.RequestContext;
import platform.core.common.service.domain.booking.model.Booking;

public interface BookingPaymentQueryService {

    Booking getBookingPaymentContext(String bookingCode, RequestContext context);
}
