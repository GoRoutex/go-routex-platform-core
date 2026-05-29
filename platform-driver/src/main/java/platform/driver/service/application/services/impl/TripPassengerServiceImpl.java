package platform.driver.service.application.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import platform.driver.service.application.dto.passengers.PassengerCheckinCommand;
import platform.driver.service.application.dto.passengers.PassengerCheckinResult;
import platform.driver.service.application.services.TripPassengerService;
import platform.core.common.service.domain.booking.port.BookingRepositoryPort;
import platform.core.common.service.domain.booking.port.BookingSeatRepositoryPort;


@RequiredArgsConstructor
@Service
public class TripPassengerServiceImpl implements TripPassengerService {


    private final BookingSeatRepositoryPort bookingSeatRepositoryPort;
    private final BookingRepositoryPort bookingRepositoryPort;

    @Override
    public PassengerCheckinResult checkInAction(PassengerCheckinCommand command) {
        return null;
    }
}
