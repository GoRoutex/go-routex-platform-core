package platform.management.service.application.services;

import platform.management.service.application.command.seat.SearchSeatCommand;
import platform.management.service.application.command.seat.SearchSeatResult;

public interface RouteSeatService {

    SearchSeatResult searchSeat(SearchSeatCommand command);
}
