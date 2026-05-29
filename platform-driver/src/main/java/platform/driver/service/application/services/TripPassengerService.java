package platform.driver.service.application.services;

import platform.driver.service.application.dto.passengers.PassengerCheckinCommand;
import platform.driver.service.application.dto.passengers.PassengerCheckinResult;

public interface TripPassengerService {
    PassengerCheckinResult checkInAction(PassengerCheckinCommand command);
}
