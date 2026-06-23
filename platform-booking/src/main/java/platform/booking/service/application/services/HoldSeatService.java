package platform.booking.service.application.services;

import platform.booking.service.application.command.seat.HoldRoundTripSeatCommand;
import platform.booking.service.application.command.seat.HoldRoundTripSeatResult;
import platform.booking.service.application.command.seat.HoldSeatCommand;
import platform.booking.service.application.command.seat.HoldSeatResult;

public interface HoldSeatService {

    HoldSeatResult holdSeat(HoldSeatCommand command);

    HoldRoundTripSeatResult holdRoundTripSeat(HoldRoundTripSeatCommand command);
}
