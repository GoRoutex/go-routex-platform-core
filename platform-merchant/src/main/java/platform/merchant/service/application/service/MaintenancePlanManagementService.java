package platform.merchant.service.application.service;

import platform.merchant.service.application.command.maintenance.CreateMaintenancePlanCommand;
import platform.merchant.service.application.command.maintenance.CreateMaintenancePlanResult;
import platform.merchant.service.application.command.maintenance.DeleteMaintenancePlanCommand;
import platform.merchant.service.application.command.maintenance.DeleteMaintenancePlanResult;
import platform.merchant.service.application.command.maintenance.FetchMaintenancePlanDetailQuery;
import platform.merchant.service.application.command.maintenance.FetchMaintenancePlanDetailResult;
import platform.merchant.service.application.command.maintenance.FetchMaintenancePlansQuery;
import platform.merchant.service.application.command.maintenance.FetchMaintenancePlansResult;
import platform.merchant.service.application.command.maintenance.UpdateMaintenancePlanCommand;
import platform.merchant.service.application.command.maintenance.UpdateMaintenancePlanResult;

public interface MaintenancePlanManagementService {
    CreateMaintenancePlanResult createMaintenancePlan(CreateMaintenancePlanCommand command);

    UpdateMaintenancePlanResult updateMaintenancePlan(UpdateMaintenancePlanCommand command);

    DeleteMaintenancePlanResult deleteMaintenancePlan(DeleteMaintenancePlanCommand command);

    FetchMaintenancePlansResult fetchMaintenancePlans(FetchMaintenancePlansQuery query);

    FetchMaintenancePlanDetailResult fetchMaintenancePlanDetail(FetchMaintenancePlanDetailQuery query);
}
