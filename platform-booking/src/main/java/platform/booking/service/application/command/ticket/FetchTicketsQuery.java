package platform.booking.service.application.command.ticket;

import lombok.Builder;
import platform.core.common.service.common.RequestContext;

@Builder
public record FetchTicketsQuery(
        RequestContext metadata,
        String customerId,
        String pageNumber,
        String pageSize
) {
}
