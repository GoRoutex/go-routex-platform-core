package platform.booking.service.domain.tripcontext.port;

import platform.booking.service.domain.tripcontext.model.TripBookingContext;
import platform.core.common.service.common.RequestContext;

public interface TripBookingContextQueryPort {

    TripBookingContext fetchByTripId(String tripId, RequestContext context);
}
