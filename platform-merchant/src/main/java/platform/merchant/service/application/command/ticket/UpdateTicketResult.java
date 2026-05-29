package platform.merchant.service.application.command.ticket;

import lombok.Builder;
import platform.core.common.service.domain.ticket.TicketStatus;

@Builder
public record UpdateTicketResult(
        String ticketId,
        TicketStatus status
) {}
