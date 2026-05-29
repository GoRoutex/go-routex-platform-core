package platform.driver.service.application.services;

import platform.driver.service.application.dto.driver.CreateDriverProfileCommand;
import platform.driver.service.application.dto.driver.CreateDriverProfileResult;
import platform.driver.service.application.dto.driver.DeleteDriverProfileCommand;
import platform.driver.service.application.dto.driver.DeleteDriverProfileResult;
import platform.driver.service.application.dto.driver.DriverProfileDetailsView;
import platform.driver.service.application.dto.driver.GetDriverProfileQuery;
import platform.driver.service.application.dto.driver.UpdateDriverProfileCommand;
import platform.driver.service.application.dto.driver.UpdateDriverProfileResult;
import platform.driver.service.application.dto.driver.UpdateDriverStatusCommand;
import platform.driver.service.application.dto.driver.UpdateDriverStatusResult;

public interface DriverProfileService {
    CreateDriverProfileResult create(CreateDriverProfileCommand command);
    UpdateDriverProfileResult update(UpdateDriverProfileCommand command);
    DeleteDriverProfileResult delete(DeleteDriverProfileCommand command);
    UpdateDriverStatusResult updateStatus(UpdateDriverStatusCommand command);
    DriverProfileDetailsView get(GetDriverProfileQuery query);
}
