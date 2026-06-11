package platform.merchant.service.application.service;


import platform.merchant.service.application.command.ticket.CreateTicketCommand;
import platform.merchant.service.application.command.ticket.CreateTicketResult;
import platform.merchant.service.application.command.ticket.FetchCustomerTicketsQuery;
import platform.merchant.service.application.command.ticket.FetchTicketDetailQuery;
import platform.merchant.service.application.command.ticket.FetchTicketDetailResult;
import platform.merchant.service.application.command.ticket.FetchTicketListQuery;
import platform.merchant.service.application.command.ticket.FetchTicketListResult;
import platform.merchant.service.application.command.ticket.SearchTicketListQuery;
import platform.merchant.service.application.command.ticket.UpdateTicketCommand;
import platform.merchant.service.application.command.ticket.UpdateTicketResult;

import java.util.List;

public interface TicketService {
    List<CreateTicketResult> createTickets(List<CreateTicketCommand> commands);
    UpdateTicketResult updateTicket(UpdateTicketCommand command);
    FetchTicketDetailResult getTicketDetail(FetchTicketDetailQuery query);
    FetchTicketListResult getTickets(FetchTicketListQuery query);
    FetchTicketListResult searchTickets(SearchTicketListQuery query);
    FetchTicketListResult getCustomerTickets(FetchCustomerTicketsQuery query);
    FetchTicketDetailResult getCustomerTicketDetail(FetchTicketDetailQuery query);
}
