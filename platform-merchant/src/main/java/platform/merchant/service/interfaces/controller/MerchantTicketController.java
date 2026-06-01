package platform.merchant.service.interfaces.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import platform.core.common.service.api.BaseRequest;
import platform.core.common.service.domain.ticket.TicketStatus;
import platform.core.common.service.domain.ticket.model.Ticket;
import platform.core.common.service.persistence.utils.ApiRequestUtils;
import platform.merchant.service.application.command.ticket.FetchTicketDetailQuery;
import platform.merchant.service.application.command.ticket.FetchTicketDetailResult;
import platform.merchant.service.application.command.ticket.FetchTicketListQuery;
import platform.merchant.service.application.command.ticket.FetchTicketListResult;
import platform.merchant.service.application.command.ticket.UpdateTicketCommand;
import platform.merchant.service.application.command.ticket.UpdateTicketResult;
import platform.merchant.service.application.service.TicketService;
import platform.merchant.service.infrastructure.persistence.utils.HttpUtils;
import platform.merchant.service.interfaces.factory.ApiResultFactory;
import platform.merchant.service.interfaces.model.ticket.request.UpdateTicketRequest;
import platform.merchant.service.interfaces.model.ticket.response.FetchTicketDetailResponse;
import platform.merchant.service.interfaces.model.ticket.response.FetchTicketListResponse;
import platform.merchant.service.interfaces.model.ticket.response.TicketResponse;
import platform.merchant.service.interfaces.model.ticket.response.UpdateTicketResponse;
import vn.com.go.routex.identity.security.log.SystemLog;

import java.util.List;
import java.util.stream.Collectors;

import static platform.core.common.service.persistence.constant.ApiConstant.API_PATH;
import static platform.core.common.service.persistence.constant.ApiConstant.API_VERSION;
import static platform.core.common.service.persistence.constant.ApiConstant.DETAIL_PATH;
import static platform.core.common.service.persistence.constant.ApiConstant.FETCH_PATH;
import static platform.core.common.service.persistence.constant.ApiConstant.MERCHANT_SERVICE;
import static platform.core.common.service.persistence.constant.ApiConstant.TICKETS_PATH;
import static platform.core.common.service.persistence.constant.ApiConstant.UPDATE_PATH;


@RestController
@RequestMapping(API_PATH + API_VERSION + MERCHANT_SERVICE)
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ticket:management') or hasRole('MERCHANT_OWNER')")
public class MerchantTicketController {

    private final TicketService ticketService;
    private final ApiResultFactory apiResultFactory;
    private final SystemLog sLog = SystemLog.getLogger(this.getClass());

    @InitBinder
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.setDisallowedFields("requestId", "requestDateTime", "channel", "data");
    }

    @PostMapping(TICKETS_PATH + UPDATE_PATH)
    public ResponseEntity<UpdateTicketResponse> updateTicket(@Valid @RequestBody UpdateTicketRequest request,
                                                             HttpServletRequest servletRequest) {
        sLog.info("[TICKET-MANAGEMENT] Update Ticket Request: {}", request);

        String merchantId = ApiRequestUtils.getMerchantId(servletRequest);
        UpdateTicketCommand command = UpdateTicketCommand.builder()
                .context(HttpUtils.toContext(request, merchantId))
                .merchantId(merchantId)
                .ticketId(request.getData().getTicketId())
                .customerName(request.getData().getCustomerName())
                .customerPhone(request.getData().getCustomerPhone())
                .customerEmail(request.getData().getCustomerEmail())
                .status(request.getData().getStatus())
                .build();

        UpdateTicketResult result = ticketService.updateTicket(command);

        UpdateTicketResponse response = UpdateTicketResponse.builder()
                .result(apiResultFactory.buildSuccess())
                .data(UpdateTicketResponse.UpdateTicketResponseData.builder()
                        .ticketId(result.ticketId())
                        .status(result.status())
                        .build())
                .build();

        return HttpUtils.buildResponse(request, response);
    }

    @GetMapping(TICKETS_PATH + DETAIL_PATH)
    public ResponseEntity<FetchTicketDetailResponse> fetchDetail(
            @RequestParam String ticketId,
            HttpServletRequest servletRequest
    ) {
        BaseRequest baseRequest = ApiRequestUtils.getBaseRequestOrDefault(servletRequest);
        String merchantId = ApiRequestUtils.getMerchantId(servletRequest);

        FetchTicketDetailQuery query = FetchTicketDetailQuery.builder()
                .context(HttpUtils.toContext(baseRequest, merchantId))
                .merchantId(merchantId)
                .ticketId(ticketId)
                .build();

        FetchTicketDetailResult result = ticketService.getTicketDetail(query);
        var t = result.ticket();

        FetchTicketDetailResponse response = FetchTicketDetailResponse.builder()
                .result(apiResultFactory.buildSuccess())
                .data(TicketResponse.builder()
                        .id(t.getId())
                        .ticketCode(t.getTicketCode())
                        .bookingId(t.getBookingId())
                        .merchantId(t.getMerchantId())
                        .tripId(t.getTripId())
                        .vehicleId(t.getVehicleId())
                        .seatNumber(t.getSeatNumber())
                        .customerName(t.getCustomerName())
                        .customerPhone(t.getCustomerPhone())
                        .customerEmail(t.getCustomerEmail())
                        .price(t.getPrice())
                        .status(t.getStatus())
                        .issuedAt(t.getIssuedAt())
                        .checkedInAt(t.getCheckedInAt())
                        .boardedAt(t.getBoardedAt())
                        .cancelledAt(t.getCancelledAt())
                        .pickupType(t.getPickupType())
                        .pickupStopId(t.getPickupStopId())
                        .pickupAddress(t.getPickupAddress())
                        .dropOffType(t.getDropOffType())
                        .dropOffStopId(t.getDropOffStopId())
                        .dropOffAddress(t.getDropOffAddress())
                        .build())
                .build();

        return HttpUtils.buildResponse(baseRequest, response);
    }

    @GetMapping(TICKETS_PATH + FETCH_PATH)
    public ResponseEntity<FetchTicketListResponse> fetchList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String query,
            @RequestParam(required = false) TicketStatus status,
            HttpServletRequest servletRequest
    ) {
        BaseRequest baseRequest = ApiRequestUtils.getBaseRequestOrDefault(servletRequest);
        String merchantId = ApiRequestUtils.getMerchantId(servletRequest);

        FetchTicketListQuery fetchQuery = FetchTicketListQuery.builder()
                .context(HttpUtils.toContext(baseRequest, merchantId))
                .merchantId(merchantId)
                .query(query)
                .status(status)
                .pageNumber(page)
                .pageSize(size)
                .build();

        FetchTicketListResult result = ticketService.getTickets(fetchQuery);

        List<TicketResponse> items = result.items().stream()
                .map(this::mapToTicketResponse)
                .collect(Collectors.toList());

        FetchTicketListResponse response = FetchTicketListResponse.builder()
                .result(apiResultFactory.buildSuccess())
                .data(FetchTicketListResponse.FetchTicketListResponsePage.builder()
                        .items(items)
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


    private TicketResponse mapToTicketResponse(Ticket ticket) {
        return TicketResponse.builder()
                .id(ticket.getId())
                .ticketCode(ticket.getTicketCode())
                .bookingId(ticket.getBookingId())
                .tripId(ticket.getTripId())
                .vehicleId(ticket.getVehicleId())
                .seatNumber(ticket.getSeatNumber())
                .customerName(ticket.getCustomerName())
                .customerPhone(ticket.getCustomerPhone())
                .customerEmail(ticket.getCustomerEmail())
                .price(ticket.getPrice())
                .status(ticket.getStatus())
                .issuedAt(ticket.getIssuedAt())
                .checkedInAt(ticket.getCheckedInAt())
                .boardedAt(ticket.getBoardedAt())
                .cancelledAt(ticket.getCancelledAt())
                .pickupType(ticket.getPickupType())
                .pickupStopId(ticket.getPickupStopId())
                .pickupAddress(ticket.getPickupAddress())
                .dropOffType(ticket.getDropOffType())
                .dropOffStopId(ticket.getDropOffStopId())
                .dropOffAddress(ticket.getDropOffAddress())
                .build();
    }
}
