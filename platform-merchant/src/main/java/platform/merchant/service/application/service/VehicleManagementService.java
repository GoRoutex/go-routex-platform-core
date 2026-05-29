package platform.merchant.service.application.service;


import platform.merchant.service.application.command.vehicle.AddVehicleCommand;
import platform.merchant.service.application.command.vehicle.AddVehicleResult;
import platform.merchant.service.application.command.vehicle.DeleteVehicleCommand;
import platform.merchant.service.application.command.vehicle.DeleteVehicleResult;
import platform.merchant.service.application.command.vehicle.FetchVehicleDetailQuery;
import platform.merchant.service.application.command.vehicle.FetchVehicleDetailResult;
import platform.merchant.service.application.command.vehicle.FetchVehiclesQuery;
import platform.merchant.service.application.command.vehicle.FetchVehiclesResult;
import platform.merchant.service.application.command.vehicle.UpdateVehicleCommand;
import platform.merchant.service.application.command.vehicle.UpdateVehicleResult;

public interface VehicleManagementService {

    AddVehicleResult addVehicle(AddVehicleCommand command);

    UpdateVehicleResult updateVehicle(UpdateVehicleCommand command);

    DeleteVehicleResult deleteVehicle(DeleteVehicleCommand command);

    FetchVehiclesResult fetchVehicles(FetchVehiclesQuery query);

    FetchVehicleDetailResult fetchVehicleDetail(FetchVehicleDetailQuery query);
}
