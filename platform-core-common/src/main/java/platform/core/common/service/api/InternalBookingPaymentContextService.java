package platform.core.common.service.api;

import platform.core.common.service.common.RequestContext;

public interface InternalBookingPaymentContextService {
    BookingPaymentContextResponse getBookingPaymentContext(String bookingCode, RequestContext context);
}
