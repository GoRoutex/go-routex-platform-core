package platform.merchant.service.application.command.ticket;

import lombok.Builder;
import platform.core.common.service.domain.ticket.model.Ticket;

import java.util.List;

@Builder
public record FetchTicketListResult(
        List<Ticket> items,
        int pageNumber,
        int pageSize,
        long totalElements,
        int totalPages
) {}
