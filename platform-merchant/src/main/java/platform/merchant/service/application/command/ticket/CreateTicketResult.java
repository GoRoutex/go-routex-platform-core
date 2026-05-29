package platform.merchant.service.application.command.ticket;

import lombok.Builder;
import platform.core.common.service.domain.ticket.TicketStatus;

@Builder
public record CreateTicketResult(
        String ticketId,
        String ticketCode,
        String bookingSeatId,
        TicketStatus status
) {}
