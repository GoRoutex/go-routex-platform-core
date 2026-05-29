package platform.booking.service.application.services;

import platform.booking.service.application.command.ticket.FetchTicketDetailQuery;
import platform.booking.service.application.command.ticket.FetchTicketDetailResult;
import platform.booking.service.application.command.ticket.FetchTicketsQuery;
import platform.booking.service.application.command.ticket.FetchTicketsResult;

public interface TicketQueryService {
    FetchTicketsResult fetchTickets(FetchTicketsQuery query);

    FetchTicketDetailResult fetchTicketDetail(FetchTicketDetailQuery query);
}
