package platform.merchant.service.application.command.ticket;

import lombok.Builder;
import platform.core.common.service.common.RequestContext;
import platform.core.common.service.domain.ticket.TicketStatus;

@Builder
public record UpdateTicketCommand(
        RequestContext context,
        String merchantId,
        String ticketId,
        String customerName,
        String customerPhone,
        String customerEmail,
        TicketStatus status
) {}
