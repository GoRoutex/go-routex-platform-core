package platform.core.common.service.api;

import platform.core.common.service.common.RequestContext;

public interface InternalBookingContextService {
    TripBookingContextResponse getTripBookingContext(String tripId, RequestContext context);
    VehicleSeatBlueprintResponse getVehicleSeatBlueprint(String blueprintId, RequestContext context);
}
