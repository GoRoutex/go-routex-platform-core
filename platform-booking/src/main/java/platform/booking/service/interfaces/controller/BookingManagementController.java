package platform.booking.service.interfaces.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import platform.booking.service.application.command.ticket.FetchTicketDetailQuery;
import platform.booking.service.application.command.ticket.FetchTicketDetailResult;
import platform.booking.service.application.command.ticket.FetchTicketsQuery;
import platform.booking.service.application.command.ticket.FetchTicketsResult;
import platform.booking.service.application.services.TicketQueryService;
import platform.booking.service.interfaces.models.ticket.FetchTicketDetailRequest;
import platform.booking.service.interfaces.models.ticket.FetchTicketDetailResponse;
import platform.booking.service.interfaces.models.ticket.FetchTicketsRequest;
import platform.booking.service.interfaces.models.ticket.FetchTicketsResponse;
import platform.core.common.service.api.ApiResult;
import platform.core.common.service.api.BaseRequest;
import platform.core.common.service.common.RequestContext;
import platform.core.common.service.persistence.utils.HttpUtils;

import java.util.List;

import static platform.core.common.service.persistence.constant.ApiConstant.API_PATH;
import static platform.core.common.service.persistence.constant.ApiConstant.API_VERSION;
import static platform.core.common.service.persistence.constant.ApiConstant.BOOKING_PATH;
import static platform.core.common.service.persistence.constant.ApiConstant.DETAIL_PATH;
import static platform.core.common.service.persistence.constant.ApiConstant.FETCH_PATH;
import static platform.core.common.service.persistence.constant.ApiConstant.TICKETS_PATH;
import static platform.core.common.service.persistence.constant.ErrorConstant.SUCCESS_CODE;
import static platform.core.common.service.persistence.constant.ErrorConstant.SUCCESS_MESSAGE;

@RestController
@RequestMapping(API_PATH + API_VERSION + BOOKING_PATH)
@RequiredArgsConstructor
public class BookingManagementController {

    private final TicketQueryService ticketQueryService;

    @InitBinder
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.setDisallowedFields("requestId", "requestDateTime", "channel", "data");
    }

    @PostMapping(TICKETS_PATH + FETCH_PATH)
    public ResponseEntity<FetchTicketsResponse> fetchTickets(@Valid @RequestBody FetchTicketsRequest request) {
        FetchTicketsResult result = ticketQueryService.fetchTickets(FetchTicketsQuery.builder()
                .metadata(toMetadata(request))
                .customerId(request.getData().getCustomerId())
                .pageNumber(request.getData().getPageNumber())
                .pageSize(request.getData().getPageSize())
                .build());

        List<FetchTicketsResponse.FetchTicketsResponseData> items = result.items().stream()
                .map(item -> new FetchTicketsResponse.FetchTicketsResponseData(
                        item.id(),
                        item.ticketCode(),
                        item.bookingId(),
                        item.bookingSeatId(),
                        item.tripId(),
                        item.seatNumber(),
                        item.customerName(),
                        item.customerPhone(),
                        item.price(),
                        item.status(),
                        item.issuedAt()))
                .toList();

        FetchTicketsResponse response = FetchTicketsResponse.builder()
                .result(successResult())
                .data(FetchTicketsResponse.FetchTicketsResponsePage.builder()
                        .items(items)
                        .pagination(FetchTicketsResponse.Pagination.builder()
                                .pageNumber(result.pageNumber())
                                .pageSize(result.pageSize())
                                .totalElements(result.totalElements())
                                .totalPages(result.totalPages())
                                .build())
                        .build())
                .build();

        return HttpUtils.buildResponse(request, response);
    }

    @PostMapping(TICKETS_PATH + DETAIL_PATH)
    public ResponseEntity<FetchTicketDetailResponse> fetchTicketDetail(@Valid @RequestBody FetchTicketDetailRequest request) {
        FetchTicketDetailResult result = ticketQueryService.fetchTicketDetail(FetchTicketDetailQuery.builder()
                .metadata(toMetadata(request))
                .customerId(request.getData().getCustomerId())
                .ticketId(request.getData().getTicketId())
                .build());

        FetchTicketDetailResponse response = FetchTicketDetailResponse.builder()
                .result(successResult())
                .data(FetchTicketDetailResponse.FetchTicketDetailResponseData.builder()
                        .id(result.id())
                        .ticketCode(result.ticketCode())
                        .bookingId(result.bookingId())
                        .bookingSeatId(result.bookingSeatId())
                        .tripId(result.tripId())
                        .seatNumber(result.seatNumber())
                        .customerName(result.customerName())
                        .customerPhone(result.customerPhone())
                        .price(result.price())
                        .status(result.status())
                        .issuedAt(result.issuedAt())
                        .checkedInAt(result.checkedInAt())
                        .boardedAt(result.boardedAt())
                        .cancelledAt(result.cancelledAt())
                        .checkedInBy(result.checkedInBy())
                        .boardedBy(result.boardedBy())
                        .cancelledBy(result.cancelledBy())
                        .build())
                .build();

        return HttpUtils.buildResponse(request, response);
    }

    private RequestContext toMetadata(BaseRequest request) {
        return RequestContext.builder()
                .requestId(request.getRequestId())
                .requestDateTime(request.getRequestDateTime())
                .channel(request.getChannel())
                .build();
    }

    private ApiResult successResult() {
        return ApiResult.builder()
                .responseCode(SUCCESS_CODE)
                .description(SUCCESS_MESSAGE)
                .build();
    }
}
