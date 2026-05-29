package platform.merchant.service.application.command.ticket;

import lombok.Builder;
import platform.core.common.service.domain.ticket.model.Ticket;

@Builder
public record FetchTicketDetailResult(
        Ticket ticket
) {}
