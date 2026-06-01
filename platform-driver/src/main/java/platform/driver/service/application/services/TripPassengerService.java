package platform.driver.service.application.services;

import platform.driver.service.application.dto.passengers.PassengerCheckinCommand;
import platform.driver.service.application.dto.passengers.PassengerCheckinResult;
import platform.driver.service.application.dto.passengers.TripLifecycleCommand;
import platform.driver.service.application.dto.passengers.TripLifecycleResult;

public interface TripPassengerService {
    PassengerCheckinResult checkInAction(PassengerCheckinCommand command);
    PassengerCheckinResult boardAction(PassengerCheckinCommand command);
    TripLifecycleResult startTrip(TripLifecycleCommand command);
    TripLifecycleResult completeTrip(TripLifecycleCommand command);
}
