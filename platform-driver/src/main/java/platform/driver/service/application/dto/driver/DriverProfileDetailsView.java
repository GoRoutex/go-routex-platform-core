package platform.driver.service.application.dto.driver;

import platform.driver.service.domain.user.model.User;
import platform.merchant.service.domain.driver.model.DriverProfile;

public record DriverProfileDetailsView(DriverProfile profile, User user) {
}
