package platform.merchant.service.application.command.ticket;

import lombok.Builder;
import platform.core.common.service.common.RequestContext;
import platform.core.common.service.domain.ticket.TicketStatus;

@Builder
public record SearchTicketListQuery(
        RequestContext context,
        String merchantId,
        String keyword,
        TicketStatus status,
        String month,
        int pageNumber,
        int pageSize
) {
}
