package platform.booking.service.application.command.ticket;

import lombok.Builder;
import platform.core.common.service.common.RequestContext;

@Builder
public record FetchTicketDetailQuery(
        RequestContext metadata,
        String customerId,
        String ticketId
) {
}
