package platform.management.service.application.services;

import platform.management.service.application.command.seat.SearchRoundTripSeatResult;
import platform.management.service.application.command.seat.SearchSeatCommand;
import platform.management.service.application.command.seat.SearchSeatResult;

public interface TripSeatService {

    SearchSeatResult searchSeat(SearchSeatCommand command);

    SearchRoundTripSeatResult searchRoundTripSeat(SearchSeatCommand outboundCommand, SearchSeatCommand returnCommand);
}
