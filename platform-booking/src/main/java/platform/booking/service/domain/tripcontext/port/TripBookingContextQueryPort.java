package platform.booking.service.domain.tripcontext.port;

import platform.core.common.service.common.RequestContext;
import platform.booking.service.domain.tripcontext.model.TripBookingContext;

public interface TripBookingContextQueryPort {

    TripBookingContext fetchByTripId(String tripId, RequestContext context);
}
