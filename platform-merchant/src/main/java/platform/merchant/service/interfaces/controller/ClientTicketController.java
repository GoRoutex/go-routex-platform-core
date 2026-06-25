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
import platform.core.common.service.domain.ticket.model.Ticket;
import platform.core.common.service.persistence.utils.ApiRequestUtils;
import platform.merchant.service.application.command.ticket.FetchCustomerTicketsQuery;
import platform.merchant.service.application.command.ticket.FetchTicketDetailQuery;
import platform.merchant.service.application.command.ticket.FetchTicketDetailResult;
import platform.merchant.service.application.command.ticket.FetchTicketListResult;
import platform.merchant.service.application.service.TicketService;
import platform.merchant.service.domain.assignment.model.TripAssignmentRecord;
import platform.merchant.service.domain.assignment.port.TripAssignmentRepositoryPort;
import platform.merchant.service.domain.route.model.RouteAggregate;
import platform.merchant.service.domain.route.port.RouteAggregateRepositoryPort;
import platform.merchant.service.domain.trip.model.TripAggregate;
import platform.merchant.service.domain.trip.port.TripAggregateRepositoryPort;
import platform.merchant.service.infrastructure.persistence.utils.HttpUtils;
import platform.merchant.service.interfaces.factory.ApiResultFactory;
import platform.merchant.service.interfaces.model.ticket.response.FetchTicketDetailResponse;
import platform.merchant.service.interfaces.model.ticket.response.FetchTicketListResponse;
import platform.merchant.service.interfaces.model.ticket.response.TicketResponse;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
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
    private final TripAggregateRepositoryPort tripAggregateRepositoryPort;
    private final RouteAggregateRepositoryPort routeAggregateRepositoryPort;
    private final TripAssignmentRepositoryPort tripAssignmentRepositoryPort;

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
                .customerId(context.userId())
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
                        .items(mapToTicketResponses(result.items()))
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
                .data(mapToTicketResponses(List.of(t)).getFirst())
                .build();

        return HttpUtils.buildResponse(baseRequest, response);
    }

    private List<TicketResponse> mapToTicketResponses(List<Ticket> tickets) {
        List<String> tripIds = tickets.stream()
                .map(Ticket::getTripId)
                .filter(this::hasText)
                .distinct()
                .toList();

        Map<String, TripAggregate> tripMap = tripIds.isEmpty()
                ? Map.of()
                : tripAggregateRepositoryPort.findByIds(tripIds).stream()
                .collect(Collectors.toMap(TripAggregate::getId, Function.identity()));

        List<String> routeIds = tripMap.values().stream()
                .map(TripAggregate::getRouteId)
                .filter(this::hasText)
                .distinct()
                .toList();

        Map<String, RouteAggregate> routeMap = routeIds.isEmpty()
                ? Map.of()
                : routeAggregateRepositoryPort.findAllByIdIn(routeIds);

        Map<String, TripAssignmentRecord> assignmentMap = tripIds.isEmpty()
                ? Map.of()
                : tripAssignmentRepositoryPort.findLatestActiveByTripIds(tripIds);

        return tickets.stream()
                .map(ticket -> {
                    TripAggregate trip = tripMap.get(ticket.getTripId());
                    RouteAggregate route = trip == null ? null : routeMap.get(trip.getRouteId());
                    TripAssignmentRecord assignment = assignmentMap.get(ticket.getTripId());
                    return mapToTicketResponse(ticket, trip, route, assignment);
                })
                .toList();
    }

    private TicketResponse mapToTicketResponse(Ticket ticket,
                                               TripAggregate trip,
                                               RouteAggregate route,
                                               TripAssignmentRecord assignment) {
        return TicketResponse.builder()
                .id(ticket.getId())
                .ticketCode(ticket.getTicketCode())
                .bookingId(ticket.getBookingId())
                .merchantId(ticket.getMerchantId())
                .tripId(ticket.getTripId())
                .routeId(trip == null ? null : trip.getRouteId())
                .originCode(route == null ? null : route.getOriginCode())
                .originName(route == null ? null : route.getOriginName())
                .destinationCode(route == null ? null : route.getDestinationCode())
                .destinationName(route == null ? null : route.getDestinationName())
                .originDepartmentId(route == null ? null : route.getOriginDepartmentId())
                .originDepartmentName(route == null ? null : route.getOriginDepartmentName())
                .destinationDepartmentId(route == null ? null : route.getDestinationDepartmentId())
                .destinationDepartmentName(route == null ? null : route.getDestinationDepartmentName())
                .duration(route == null ? null : route.getDuration())
                .vehicleId(hasText(ticket.getVehicleId()) ? ticket.getVehicleId() : assignment == null ? null : assignment.getVehicleId())
                .seatNumber(ticket.getSeatNumber())
                .customerId(ticket.getCustomerId())
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

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
