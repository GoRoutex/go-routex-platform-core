package platform.booking.service.domain.vehicle.port;

import platform.core.common.service.common.RequestContext;
import platform.core.common.service.domain.vehicle.model.VehicleSeatBlueprint;

public interface VehicleSeatBlueprintQueryPort {

    VehicleSeatBlueprint fetchByVehicleId(String vehicleId, RequestContext context);
}
