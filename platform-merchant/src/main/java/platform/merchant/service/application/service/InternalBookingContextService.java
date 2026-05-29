package platform.merchant.service.application.service;

import platform.core.common.service.common.RequestContext;
import platform.merchant.service.interfaces.model.internal.booking.InternalBookingContextResponses;

public interface InternalBookingContextService {

    InternalBookingContextResponses.TripBookingContextData fetchTripBookingContext(String tripId, RequestContext context);

    InternalBookingContextResponses.VehicleSeatBlueprintData fetchVehicleSeatBlueprint(String vehicleId, RequestContext context);
}
