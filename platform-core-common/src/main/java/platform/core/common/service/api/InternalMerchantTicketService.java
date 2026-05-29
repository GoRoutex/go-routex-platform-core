package platform.core.common.service.api;

import platform.core.common.service.common.RequestContext;
import platform.core.common.service.domain.ticket.model.Ticket;

import java.util.List;

public interface InternalMerchantTicketService {
    List<Ticket> createTickets(List<Ticket> ticketsToCreate, RequestContext context);
}
