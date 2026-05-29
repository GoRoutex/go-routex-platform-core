package platform.booking.service.application.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import platform.booking.service.application.command.ticket.FetchTicketDetailQuery;
import platform.booking.service.application.command.ticket.FetchTicketDetailResult;
import platform.booking.service.application.command.ticket.FetchTicketsQuery;
import platform.booking.service.application.command.ticket.FetchTicketsResult;
import platform.booking.service.application.services.TicketQueryService;
import platform.core.common.service.application.command.common.PagedResult;
import platform.core.common.service.domain.ticket.model.Ticket;
import platform.core.common.service.domain.ticket.port.TicketRepositoryPort;
import platform.core.common.service.persistence.exception.BusinessException;
import platform.core.common.service.persistence.utils.ExceptionUtils;

import java.util.List;

import static platform.core.common.service.persistence.constant.ErrorConstant.INVALID_INPUT_ERROR;
import static platform.core.common.service.persistence.constant.ErrorConstant.INVALID_PAGE_NUMBER;
import static platform.core.common.service.persistence.constant.ErrorConstant.INVALID_PAGE_SIZE;
import static platform.core.common.service.persistence.constant.ErrorConstant.RECORD_NOT_FOUND;
import static platform.core.common.service.persistence.constant.ErrorConstant.TICKET_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class TicketQueryServiceImpl implements TicketQueryService {

    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final int DEFAULT_PAGE_NUMBER = 1;

    private final TicketRepositoryPort ticketRepositoryPort;

    @Override
    public FetchTicketsResult fetchTickets(FetchTicketsQuery query) {
        int pageSize = parsePositiveIntOrDefault(query.pageSize(), DEFAULT_PAGE_SIZE);
        int pageNumber = parsePositiveIntOrDefault(query.pageNumber(), DEFAULT_PAGE_NUMBER);

        if (pageSize < 1 || pageSize > 100) {
            throw new BusinessException(query.metadata().requestId(), query.metadata().requestDateTime(), query.metadata().channel(),
                    ExceptionUtils.buildResultResponse(INVALID_INPUT_ERROR, INVALID_PAGE_SIZE));
        }
        if (pageNumber < 1) {
            throw new BusinessException(query.metadata().requestId(), query.metadata().requestDateTime(), query.metadata().channel(),
                    ExceptionUtils.buildResultResponse(INVALID_INPUT_ERROR, INVALID_PAGE_NUMBER));
        }

        PagedResult<Ticket> page = ticketRepositoryPort.fetchByCustomerId(query.customerId(), pageNumber - 1, pageSize);
        List<FetchTicketsResult.TicketItemResult> items = page.getItems().stream()
                .map(ticket -> FetchTicketsResult.TicketItemResult.builder()
                        .id(ticket.getId())
                        .ticketCode(ticket.getTicketCode())
                        .bookingId(ticket.getBookingId())
                        .bookingSeatId(ticket.getBookingSeatId())
                        .tripId(ticket.getTripId())
                        .seatNumber(ticket.getSeatNumber())
                        .customerName(ticket.getCustomerName())
                        .customerPhone(ticket.getCustomerPhone())
                        .price(ticket.getPrice())
                        .status(ticket.getStatus())
                        .issuedAt(ticket.getIssuedAt())
                        .build())
                .toList();

        return FetchTicketsResult.builder()
                .items(items)
                .pageNumber(page.getPageNumber() + 1)
                .pageSize(page.getPageSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
    }

    private int parsePositiveIntOrDefault(String rawValue, int defaultValue) {
        if (rawValue == null || rawValue.isBlank()) {
            return defaultValue;
        }

        try {
            return Integer.parseInt(rawValue.trim());
        } catch (NumberFormatException exception) {
            return defaultValue;
        }
    }

    @Override
    public FetchTicketDetailResult fetchTicketDetail(FetchTicketDetailQuery query) {
        Ticket ticket = ticketRepositoryPort.findByIdAndCustomerId(query.ticketId(), query.customerId())
                .orElseThrow(() -> new BusinessException(query.metadata().requestId(), query.metadata().requestDateTime(), query.metadata().channel(),
                        ExceptionUtils.buildResultResponse(RECORD_NOT_FOUND, String.format(TICKET_NOT_FOUND, query.ticketId()))));

        return FetchTicketDetailResult.builder()
                .id(ticket.getId())
                .ticketCode(ticket.getTicketCode())
                .bookingId(ticket.getBookingId())
                .bookingSeatId(ticket.getBookingSeatId())
                .tripId(ticket.getTripId())
                .seatNumber(ticket.getSeatNumber())
                .customerName(ticket.getCustomerName())
                .customerPhone(ticket.getCustomerPhone())
                .price(ticket.getPrice())
                .status(ticket.getStatus())
                .issuedAt(ticket.getIssuedAt())
                .checkedInAt(ticket.getCheckedInAt())
                .boardedAt(ticket.getBoardedAt())
                .cancelledAt(ticket.getCancelledAt())
                .checkedInBy(ticket.getCheckedInBy())
                .boardedBy(ticket.getBoardedBy())
                .cancelledBy(ticket.getCancelledBy())
                .build();
    }
}
