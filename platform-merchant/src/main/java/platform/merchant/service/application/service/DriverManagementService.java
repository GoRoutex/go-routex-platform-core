package platform.merchant.service.application.service;

import platform.merchant.service.application.command.driver.CreateDriverCommand;
import platform.merchant.service.application.command.driver.CreateDriverResult;
import platform.merchant.service.application.command.driver.DeleteDriverCommand;
import platform.merchant.service.application.command.driver.DeleteDriverResult;
import platform.merchant.service.application.command.driver.FetchDriverDetailQuery;
import platform.merchant.service.application.command.driver.FetchDriverDetailResult;
import platform.merchant.service.application.command.driver.FetchDriversQuery;
import platform.merchant.service.application.command.driver.FetchDriversResult;
import platform.merchant.service.application.command.driver.UpdateDriverCommand;
import platform.merchant.service.application.command.driver.UpdateDriverResult;

public interface DriverManagementService {
    CreateDriverResult createDriver(CreateDriverCommand command);

    UpdateDriverResult updateDriver(UpdateDriverCommand command);

    DeleteDriverResult deleteDriver(DeleteDriverCommand command);

    FetchDriversResult fetchDrivers(FetchDriversQuery query);

    FetchDriverDetailResult fetchDriverDetail(FetchDriverDetailQuery query);
}
