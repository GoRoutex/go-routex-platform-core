package platform.merchant.service.application.command.ticket;

import lombok.Builder;
import platform.core.common.service.common.RequestContext;

@Builder
public record FetchTicketDetailQuery(
        RequestContext context,
        String merchantId,
        String ticketId
) {}
