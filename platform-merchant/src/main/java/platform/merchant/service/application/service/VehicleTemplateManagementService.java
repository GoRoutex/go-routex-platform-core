package platform.merchant.service.application.service;

import platform.merchant.service.application.command.vehicletemplate.CreateVehicleTemplateCommand;
import platform.merchant.service.application.command.vehicletemplate.CreateVehicleTemplateResult;
import platform.merchant.service.application.command.vehicletemplate.DeleteVehicleTemplateCommand;
import platform.merchant.service.application.command.vehicletemplate.DeleteVehicleTemplateResult;
import platform.merchant.service.application.command.vehicletemplate.FetchVehicleTemplateDetailQuery;
import platform.merchant.service.application.command.vehicletemplate.FetchVehicleTemplateDetailResult;
import platform.merchant.service.application.command.vehicletemplate.FetchVehicleTemplatesQuery;
import platform.merchant.service.application.command.vehicletemplate.FetchVehicleTemplatesResult;
import platform.merchant.service.application.command.vehicletemplate.UpdateVehicleTemplateCommand;
import platform.merchant.service.application.command.vehicletemplate.UpdateVehicleTemplateResult;

public interface VehicleTemplateManagementService {

    CreateVehicleTemplateResult createVehicleTemplate(CreateVehicleTemplateCommand command);

    UpdateVehicleTemplateResult updateVehicleTemplate(UpdateVehicleTemplateCommand command);

    DeleteVehicleTemplateResult deleteVehicleTemplate(DeleteVehicleTemplateCommand command);

    FetchVehicleTemplatesResult fetchVehicleTemplates(FetchVehicleTemplatesQuery query);

    FetchVehicleTemplateDetailResult fetchVehicleTemplateDetail(FetchVehicleTemplateDetailQuery query);
}
