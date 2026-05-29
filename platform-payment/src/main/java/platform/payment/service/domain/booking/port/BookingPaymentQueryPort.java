package platform.payment.service.domain.booking.port;

import platform.core.common.service.common.RequestContext;
import platform.payment.service.domain.booking.model.BookingPaymentContext;

public interface BookingPaymentQueryPort {

    BookingPaymentContext getBookingPaymentContext(String bookingCode, RequestContext context);
}
