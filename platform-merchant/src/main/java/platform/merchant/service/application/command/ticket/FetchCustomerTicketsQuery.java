package platform.merchant.service.application.command.ticket;

import lombok.Builder;
import platform.core.common.service.common.RequestContext;

import java.time.OffsetDateTime;

@Builder
public record FetchCustomerTicketsQuery(
        RequestContext context,
        String customerEmail,
        String customerPhone,
        String ticketCode,
        OffsetDateTime fromDate,
        OffsetDateTime toDate,
        int pageNumber,
        int pageSize
) {}
