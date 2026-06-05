package platform.merchant.service.interfaces.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import platform.core.common.service.api.BaseRequest;
import platform.core.common.service.common.RequestContext;
import platform.core.common.service.persistence.utils.ApiRequestUtils;
import platform.merchant.service.application.command.ticket.FetchCustomerTicketsQuery;
import platform.merchant.service.application.command.ticket.FetchTicketDetailQuery;
import platform.merchant.service.application.command.ticket.FetchTicketDetailResult;
import platform.merchant.service.application.command.ticket.FetchTicketListResult;
import platform.merchant.service.application.service.TicketService;
import platform.merchant.service.infrastructure.persistence.utils.HttpUtils;
import platform.merchant.service.interfaces.factory.ApiResultFactory;
import platform.merchant.service.interfaces.model.ticket.response.FetchTicketDetailResponse;
import platform.merchant.service.interfaces.model.ticket.response.FetchTicketListResponse;
import platform.merchant.service.interfaces.model.ticket.response.TicketResponse;

import java.time.OffsetDateTime;
import java.util.stream.Collectors;

import static platform.core.common.service.persistence.constant.ApiConstant.API_PATH;
import static platform.core.common.service.persistence.constant.ApiConstant.API_VERSION;
import static platform.core.common.service.persistence.constant.ApiConstant.CLIENT_PATH;
import static platform.core.common.service.persistence.constant.ApiConstant.MERCHANT_SERVICE;
import static platform.core.common.service.persistence.constant.ApiConstant.TICKETS_PATH;

@RestController
@RequestMapping(API_PATH + API_VERSION + MERCHANT_SERVICE + CLIENT_PATH)
@RequiredArgsConstructor
@Tag(name = "Client Ticket Management", description = "Endpoints for customers to manage their tickets")
public class ClientTicketController {

    private final TicketService ticketService;
    private final ApiResultFactory apiResultFactory;

    @InitBinder
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.setDisallowedFields("requestId", "requestDateTime", "channel", "data");
    }

    @Operation(summary = "Fetch current user's tickets")
    @GetMapping(TICKETS_PATH)
    public ResponseEntity<FetchTicketListResponse> getMyTickets(
            @RequestParam(required = false) String ticketCode,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime toDate,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest servletRequest) {

        RequestContext context = HttpUtils.toContext(servletRequest);
        BaseRequest baseRequest = ApiRequestUtils.getBaseRequestOrDefault(servletRequest);
        
        FetchCustomerTicketsQuery query = FetchCustomerTicketsQuery.builder()
                .context(context)
                .customerEmail(context.userEmail())
                .customerPhone(context.userPhone())
                .ticketCode(ticketCode)
                .fromDate(fromDate)
                .toDate(toDate)
                .pageNumber(page)
                .pageSize(size)
                .build();

        FetchTicketListResult result = ticketService.getCustomerTickets(query);

        FetchTicketListResponse response = FetchTicketListResponse.builder()
                .result(apiResultFactory.buildSuccess())
                .data(FetchTicketListResponse.FetchTicketListResponsePage.builder()
                        .items(result.items().stream()
                                .map(t -> TicketResponse.builder()
                                        .id(t.getId())
                                        .ticketCode(t.getTicketCode())
                                        .customerName(t.getCustomerName())
                                        .customerPhone(t.getCustomerPhone())
                                        .customerEmail(t.getCustomerEmail())
                                        .price(t.getPrice())
                                        .status(t.getStatus())
                                        .issuedAt(t.getIssuedAt())
                                        .seatNumber(t.getSeatNumber())
                                        .tripId(t.getTripId())
                                        .merchantId(t.getMerchantId())
                                        .build())
                                .collect(Collectors.toList()))
                        .pagination(FetchTicketListResponse.Pagination.builder()
                                .pageNumber(result.pageNumber())
                                .pageSize(result.pageSize())
                                .totalElements(result.totalElements())
                                .totalPages(result.totalPages())
                                .build())
                        .build())
                .build();

        return HttpUtils.buildResponse(baseRequest, response);
    }

    @Operation(summary = "Get ticket detail for current user")
    @GetMapping(TICKETS_PATH + "/{ticketId}")
    public ResponseEntity<FetchTicketDetailResponse> getMyTicketDetail(
            @PathVariable String ticketId,
            HttpServletRequest servletRequest) {

        RequestContext context = HttpUtils.toContext(servletRequest);
        BaseRequest baseRequest = ApiRequestUtils.getBaseRequestOrDefault(servletRequest);

        FetchTicketDetailResult result = ticketService.getCustomerTicketDetail(FetchTicketDetailQuery.builder()
                .context(context)
                .ticketId(ticketId)
                .build());

        var t = result.ticket();
        FetchTicketDetailResponse response = FetchTicketDetailResponse.builder()
                .result(apiResultFactory.buildSuccess())
                .data(TicketResponse.builder()
                        .id(t.getId())
                        .ticketCode(t.getTicketCode())
                        .customerName(t.getCustomerName())
                        .customerPhone(t.getCustomerPhone())
                        .customerEmail(t.getCustomerEmail())
                        .price(t.getPrice())
                        .status(t.getStatus())
                        .issuedAt(t.getIssuedAt())
                        .seatNumber(t.getSeatNumber())
                        .tripId(t.getTripId())
                        .merchantId(t.getMerchantId())
                        .build())
                .build();

        return HttpUtils.buildResponse(baseRequest, response);
    }
}
