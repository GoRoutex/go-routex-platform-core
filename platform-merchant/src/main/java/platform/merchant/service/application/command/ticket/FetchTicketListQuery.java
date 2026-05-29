package platform.merchant.service.application.command.ticket;

import lombok.Builder;
import platform.core.common.service.common.RequestContext;
import platform.core.common.service.domain.ticket.TicketStatus;

@Builder
public record FetchTicketListQuery(
        RequestContext context,
        String merchantId,
        String query,
        TicketStatus status,
        int pageNumber,
        int pageSize
) {}
