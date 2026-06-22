package platform.management.service.application.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import platform.core.common.service.application.command.common.PageContext;
import platform.core.common.service.application.command.common.PageInfo;
import platform.core.common.service.application.command.common.PagedResult;
import platform.core.common.service.application.readmodel.TripSearchView;
import platform.core.common.service.common.RequestContext;
import platform.core.common.service.persistence.exception.BusinessException;
import platform.core.common.service.persistence.utils.ExceptionUtils;
import platform.management.service.application.command.trip.FetchTripQuery;
import platform.management.service.application.command.trip.FetchTripResult;
import platform.management.service.application.command.trip.FetchTripsQuery;
import platform.management.service.application.command.trip.FetchTripsResult;
import platform.management.service.application.command.trip.FetchRoundTripDetailQuery;
import platform.management.service.application.command.trip.FetchRoundTripDetailResult;
import platform.management.service.application.command.trip.RoutePointResult;
import platform.management.service.application.command.trip.SearchRoundTripQuery;
import platform.management.service.application.command.trip.SearchRoundTripResult;
import platform.management.service.application.command.trip.SearchTripItemResult;
import platform.management.service.application.command.trip.SearchTripQuery;
import platform.management.service.application.command.trip.SearchTripResult;
import platform.management.service.application.services.TripManagementService;
import platform.management.service.domain.assignment.port.TripAssignmentRepositoryPort;
import platform.management.service.domain.trip.model.TripAggregate;
import platform.management.service.domain.trip.port.TripAggregateRepositoryPort;
import platform.management.service.infrastructure.integration.common.support.InternalApiExecutor;
import platform.management.service.infrastructure.integration.merchantplatform.client.MerchantPlatformInternalClient;
import platform.management.service.infrastructure.integration.merchantplatform.model.MerchantPlatformFetchMerchantsRequest;
import platform.management.service.infrastructure.integration.merchantplatform.model.MerchantPlatformInternalModels;
import platform.management.service.infrastructure.persistence.utils.ApiRequestUtils;
import platform.merchant.service.domain.assignment.model.TripAssignmentRecord;
import platform.merchant.service.domain.department.model.Department;
import platform.merchant.service.domain.department.port.DepartmentRepositoryPort;
import platform.merchant.service.domain.route.model.RouteAggregate;
import platform.merchant.service.domain.route.model.RouteStopPlan;
import platform.merchant.service.domain.route.port.RouteAggregateRepositoryPort;
import platform.merchant.service.domain.route.port.RouteStopRepositoryPort;
import platform.merchant.service.domain.seat.port.TripSeatAvailabilityPort;
import platform.merchant.service.domain.trip.port.TripQueryPort;
import platform.merchant.service.domain.trip.readmodel.TripFetchView;
import platform.merchant.service.domain.vehicle.model.VehicleProfile;
import platform.merchant.service.domain.vehicle.port.VehicleProfileRepositoryPort;
import vn.com.go.routex.identity.security.log.SystemLog;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static platform.core.common.service.persistence.constant.ErrorConstant.INVALID_INPUT_ERROR;
import static platform.core.common.service.persistence.constant.ErrorConstant.INVALID_PAGE_NUMBER;
import static platform.core.common.service.persistence.constant.ErrorConstant.INVALID_PAGE_SIZE;
import static platform.core.common.service.persistence.constant.ErrorConstant.RECORD_NOT_FOUND;
import static platform.core.common.service.persistence.constant.ErrorConstant.ROUTE_NOT_FOUND;
import static platform.core.common.service.persistence.constant.ErrorConstant.TRIP_NOT_FOUND;
import static platform.management.service.infrastructure.persistence.constant.ApplicationConstant.DEFAULT_PAGE_NUMBER;
import static platform.management.service.infrastructure.persistence.constant.ApplicationConstant.DEFAULT_PAGE_SIZE;

@Service
@RequiredArgsConstructor
public class TripManagementServiceImpl implements TripManagementService {

    private final RouteStopRepositoryPort routePointRepositoryPort;
    private final TripAssignmentRepositoryPort tripAssignmentRepositoryPort;
    private final RouteAggregateRepositoryPort routeAggregateRepositoryPort;
    private final VehicleProfileRepositoryPort routeVehicleRepositoryPort;
    private final TripSeatAvailabilityPort tripSeatAvailabilityPort;
    private final TripQueryPort tripQueryPort;
    private final MerchantPlatformInternalClient merchantPlatformInternalClient;
    private final TripAggregateRepositoryPort tripAggregateRepositoryPort;
    private final DepartmentRepositoryPort departmentRepositoryPort;
    private final SystemLog sLog = SystemLog.getLogger(this.getClass());


    @Override
    public SearchRoundTripResult searchRoundTrip(SearchRoundTripQuery query) {
        PageInfo pageInfo = validatePageContext(query.context(), query.pageContext());
        validateRoundTripQuery(query);
        return SearchRoundTripResult.builder()
                .outBoundTrip(searchTripLeg(
                        query.outBoundTrip().originName(),
                        query.outBoundTrip().destinationName(),
                        query.outBoundTrip().departureDate(),
                        query.seat(),
                        pageInfo,
                        query.context()
                ))
                .returnTrip(searchTripLeg(
                        query.returnTrip().originName(),
                        query.returnTrip().destinationName(),
                        query.returnTrip().departureDate(),
                        query.seat(),
                        pageInfo,
                        query.context()
                ))
                .build();
    }

    @Override
    public SearchTripResult searchTrip(SearchTripQuery query) {
        PageInfo pageInfo = validatePageContext(query.context(), query.pageContext());

        List<SearchTripItemResult> items = searchTripLeg(
                query.originName(),
                query.destinationName(),
                query.departureDate(),
                query.seat(),
                pageInfo,
                query.context()
        );

        return SearchTripResult.builder()
                .data(items)
                .build();
    }

    private List<SearchTripItemResult> searchTripLeg(String originName,
                                                     String destinationName,
                                                     String departureDate,
                                                     String seat,
                                                     PageInfo pageInfo,
                                                     RequestContext context) {

        sLog.info("Origin Name: {} Destination Name: {} Departure Date: {}",originName, destinationName, departureDate);
        List<TripSearchView> searchedTrips = tripQueryPort.searchAssignedTrips(
                originName,
                destinationName,
                departureDate,
                pageInfo.pageNumber() - 1, // external is 1-based; Spring Data is 0-based
                pageInfo.pageSize()
        );

        TripEnrichment enrichment = enrichRoutes(
                searchedTrips.stream().map(t -> t.getTripInformation().getId()).toList(),
                searchedTrips.stream().map(t -> t.getRouteInformation().getRouteId()).toList(),
                searchedTrips.stream().flatMap(route -> Stream.of(
                        route.getRouteInformation().getOriginDepartmentId(),
                        route.getRouteInformation().getDestinationDepartmentId()
                ))
                        .filter(Objects::nonNull)
                        .distinct().toList()
        );

        Map<String, String> merchantNames = fetchMerchantNames(
                searchedTrips.stream().map(t -> t.getTripInformation().getMerchantId()).distinct().toList(),
                context
        );

        return searchedTrips.stream()
                .map(route -> toSearchRouteItem(route, enrichment, merchantNames))
                .filter(item -> hasEnoughSeats(item, seat))
                .collect(Collectors.toList());
    }

    private void validateRoundTripQuery(SearchRoundTripQuery query) {
        if (query.outBoundTrip() == null || query.returnTrip() == null) {
            throw new BusinessException(
                    query.context().requestId(),
                    query.context().requestDateTime(),
                    query.context().channel(),
                    ExceptionUtils.buildResultResponse(INVALID_INPUT_ERROR, "outboundData and returnData are required")
            );
        }
        if (query.outBoundTrip().departureDate() == null || query.outBoundTrip().departureDate().isBlank()
                || query.returnTrip().departureDate() == null || query.returnTrip().departureDate().isBlank()) {
            throw new BusinessException(
                    query.context().requestId(),
                    query.context().requestDateTime(),
                    query.context().channel(),
                    ExceptionUtils.buildResultResponse(INVALID_INPUT_ERROR, "departureDate is required for round trip")
            );
        }
        LocalDate outboundDate = LocalDate.parse(query.outBoundTrip().departureDate().trim());
        LocalDate returnDate = LocalDate.parse(query.returnTrip().departureDate().trim());
        if (returnDate.isBefore(outboundDate)) {
            throw new BusinessException(
                    query.context().requestId(),
                    query.context().requestDateTime(),
                    query.context().channel(),
                    ExceptionUtils.buildResultResponse(INVALID_INPUT_ERROR, "return departureDate must be on or after outbound departureDate")
            );
        }
    }

    private boolean hasEnoughSeats(SearchTripItemResult item, String seat) {
        if (seat == null || seat.isBlank()) {
            return true;
        }
        int requestedSeats = Integer.parseInt(seat.trim());
        return item.availableSeats() != null && item.availableSeats() >= requestedSeats;
    }

    private void validateRoundTripDetailQuery(FetchRoundTripDetailQuery query) {
        if (query.outboundTripId() == null || query.outboundTripId().isBlank()
                || query.returnTripId() == null || query.returnTripId().isBlank()) {
            throw new BusinessException(
                    query.requestId(),
                    query.requestDateTime(),
                    query.channel(),
                    ExceptionUtils.buildResultResponse(INVALID_INPUT_ERROR, "outboundTripId and returnTripId are required")
            );
        }
        if (query.outboundTripId().equals(query.returnTripId())) {
            throw new BusinessException(
                    query.requestId(),
                    query.requestDateTime(),
                    query.channel(),
                    ExceptionUtils.buildResultResponse(INVALID_INPUT_ERROR, "outboundTripId and returnTripId must be different")
            );
        }
    }

    @Override
    public FetchTripResult fetchTripDetail(FetchTripQuery query) {
        TripAggregate trip = tripAggregateRepositoryPort.findById(query.tripId())
                .orElseThrow(() -> new BusinessException(
                        query.requestId(),
                        query.requestDateTime(),
                        query.channel(),
                        ExceptionUtils.buildResultResponse(RECORD_NOT_FOUND, String.format(TRIP_NOT_FOUND, query.tripId()))
                ));

        RouteAggregate route = routeAggregateRepositoryPort.findById(trip.getRouteId())
                .orElseThrow(() -> new BusinessException(
                        query.requestId(),
                        query.requestDateTime(),
                        query.channel(),
                        ExceptionUtils.buildResultResponse(RECORD_NOT_FOUND, String.format(ROUTE_NOT_FOUND, query.tripId()))
                ));

        List<String> departmentIds = List.of(route.getOriginDepartmentId(), route.getDestinationDepartmentId());

        TripEnrichment enrichment = enrichRoutes(List.of(trip.getId()), List.of(trip.getRouteId()), departmentIds);

        return toFetchTripDetail(route, trip, enrichment);
    }

    @Override
    public FetchRoundTripDetailResult fetchRoundTripDetail(FetchRoundTripDetailQuery query) {
        validateRoundTripDetailQuery(query);

        FetchTripResult outboundTrip = fetchTripDetail(FetchTripQuery.builder()
                .tripId(query.outboundTripId())
                .requestId(query.requestId())
                .requestDateTime(query.requestDateTime())
                .channel(query.channel())
                .build());

        FetchTripResult returnTrip = fetchTripDetail(FetchTripQuery.builder()
                .tripId(query.returnTripId())
                .requestId(query.requestId())
                .requestDateTime(query.requestDateTime())
                .channel(query.channel())
                .build());

        if (outboundTrip.departureTime() != null
                && returnTrip.departureTime() != null
                && returnTrip.departureTime().isBefore(outboundTrip.departureTime())) {
            throw new BusinessException(
                    query.requestId(),
                    query.requestDateTime(),
                    query.channel(),
                    ExceptionUtils.buildResultResponse(INVALID_INPUT_ERROR, "returnTrip must depart on or after outboundTrip")
            );
        }

        return FetchRoundTripDetailResult.builder()
                .outboundTrip(outboundTrip)
                .returnTrip(returnTrip)
                .build();
    }

    @Override
    public FetchTripsResult fetchTrips(FetchTripsQuery query) {
        PageInfo pageInfo = validatePageContext(query.context(), query.pageContext());
        DateRange dateRange = resolveDateFilter(query.context(), query.dateFilter());

        // external is 1-based; Spring Data is 0-based
        PagedResult<TripFetchView> page = tripQueryPort.fetchTrips(
                dateRange.from(),
                dateRange.to(),
                pageInfo.pageNumber() - 1,
                pageInfo.pageSize()
        );

        List<TripFetchView> trips = page.getItems();
        TripEnrichment enrichment = enrichRoutes(
                trips.stream().map(TripFetchView::getId).toList(),
                trips.stream().map(TripFetchView::getRouteId).toList(),
                trips.stream()
                        .flatMap(trip -> Stream.of(
                                trip.getOriginDepartmentId(),
                                trip.getDestinationDepartmentId()
                        ))
                        .filter(Objects::nonNull)
                        .distinct()
                        .toList()
        );

        List<FetchTripResult> items = trips.stream()
                .map(trip -> toFetchRouteItem(trip, enrichment))
                .collect(Collectors.toList());

        return FetchTripsResult.builder()
                .items(items)
                .pageNumber(page.getPageNumber() + 1)
                .pageSize(page.getPageSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
    }

    private RoutePointResult toRoutePoint(RouteStopPlan s) {
        return RoutePointResult.builder()
                .id(s.getId())
                .stopOrder(s.getStopOrder())
                .routeId(s.getRouteId())
                .note(s.getNote())
                .departmentId(s.getDepartmentId())
                .stopName(s.getStopName())
                .stopAddress(s.getStopAddress())
                .stopCity(s.getStopCity())
                .stopLatitude(s.getStopLatitude())
                .stopLongitude(s.getStopLongitude())
                .timeAtDepartment(s.getTimeAtDepartment())
                .build();
    }

    private DateRange resolveDateFilter(RequestContext context, String dateFilter) {
        if (dateFilter == null || dateFilter.isBlank()) {
            return new DateRange(null, null);
        }

        LocalDate targetDate = switch (dateFilter.trim().toUpperCase(Locale.ROOT)) {
            case "YESTERDAY" -> LocalDate.now().minusDays(1);
            case "TODAY" -> LocalDate.now();
            case "TOMORROW" -> LocalDate.now().plusDays(1);
            default -> throw new BusinessException(
                    context.requestId(),
                    context.requestDateTime(),
                    context.channel(),
                    ExceptionUtils.buildResultResponse(INVALID_INPUT_ERROR, "dateFilter must be YESTERDAY, TODAY, or TOMORROW")
            );
        };

        ZoneId zoneId = ZoneId.systemDefault();
        OffsetDateTime from = targetDate.atStartOfDay(zoneId).toOffsetDateTime();
        OffsetDateTime to = targetDate.plusDays(1).atStartOfDay(zoneId).toOffsetDateTime();
        return new DateRange(from, to);
    }

    private record DateRange(OffsetDateTime from, OffsetDateTime to) {
    }

    private TripEnrichment enrichRoutes(List<String> tripIds, List<String> routeIds, List<String> departmentIds) {
        if (tripIds.isEmpty()) {
            return new TripEnrichment(Map.of(), Map.of(), Map.of(), Map.of(), Map.of());
        }
        Map<String, Long> seatAvailable = tripSeatAvailabilityPort.countAvailableSeats(tripIds);
        Map<String, TripAssignmentRecord> assignments = tripAssignmentRepositoryPort.findLatestActiveByTripIds(tripIds);
        List<String> vehicleIds = assignments.values().stream()
                .map(TripAssignmentRecord::getVehicleId)
                .distinct()
                .toList();
        Map<String, VehicleProfile> vehicles = routeVehicleRepositoryPort.findByIds(vehicleIds);
        Map<String, List<RouteStopPlan>> stopsByRouteId = routePointRepositoryPort.findByRouteIds(routeIds);
        List<Department> listDepartment = departmentRepositoryPort.findAllByIdIn(departmentIds);

        Map<String, Department> departmentMap = listDepartment
                .stream()
                .collect(Collectors.toMap(
                        Department::getId,
                        Function.identity()
                ));

        return new TripEnrichment(assignments, seatAvailable, vehicles, stopsByRouteId, departmentMap);
    }

    private SearchTripItemResult toSearchRouteItem(
            TripSearchView trip,
            TripEnrichment enrichment,
            Map<String, String> merchantNames
    ) {
        TripSearchView.TripInformation tripInformation = trip.getTripInformation();
        TripSearchView.TripAssignment searchAssignment = trip.getAssignment();
        TripSearchView.RouteInformation routeInformation = trip.getRouteInformation();
        TripAssignmentRecord assignment = enrichment.assignments().get(tripInformation.getId());
        VehicleProfile vehicle = findVehicle(assignment, enrichment);
        List<RouteStopPlan> routeStopPlans = enrichment.stopsByRouteId().get(routeInformation.getRouteId());
        List<RoutePointResult> routePointResults = toRoutePoints(routeStopPlans);
        sLog.info("Enrichment DepartmentMap: {}", enrichment.departmentMap);
        String originDepartmentName = enrichment.departmentMap.get(routeInformation.getOriginDepartmentId()).getName();
        String destinationDepartmentName = enrichment.departmentMap.get(routeInformation.getDestinationDepartmentId()).getName();

        return SearchTripItemResult.builder()
                .id(tripInformation.getId())
                .merchantId(tripInformation.getMerchantId())
                .vehicleId(searchAssignment.getVehicleId())
                .driverId(searchAssignment.getDriverId())
                .routeId(routeInformation.getRouteId())
                .ticketPrice(searchAssignment.getTicketPrice())
                .merchantName(merchantNames.get(tripInformation.getMerchantId()))
                .tripCode(tripInformation.getTripCode())
                .originCode(routeInformation.getOriginCode())
                .originName(routeInformation.getOriginName())
                .destinationName(routeInformation.getDestinationName())
                .destinationCode(routeInformation.getDestinationCode())
                .originProvinceId(routeInformation.getOriginProvinceId())
                .destinationProvinceId(routeInformation.getDestinationProvinceId())
                .originDepartmentId(routeInformation.getOriginDepartmentId())
                .originDepartmentName(originDepartmentName)
                .destinationDepartmentId(routeInformation.getDestinationDepartmentId())
                .destinationDepartmentName(destinationDepartmentName)
                .availableSeats(enrichment.seatAvailable().getOrDefault(tripInformation.getId(), 0L))
                .departureTime(tripInformation.getDepartureTime())
                .rawDepartureTime(tripInformation.getRawDepartureTime())
                .rawDepartureDate(tripInformation.getRawDepartureDate())
                .durationMinutes(routeInformation.getDurationMinutes())
                .vehiclePlate(vehicle == null ? null : vehicle.getVehiclePlate())
                .hasFloor(vehicle != null && vehicle.isHasFloor())
                .routePoints(routePointResults)
                .build();
    }

    private Map<String, String> fetchMerchantNames(List<String> merchantIds, RequestContext context) {
        if (merchantIds == null || merchantIds.isEmpty()) {
            return Map.of();
        }

        MerchantPlatformFetchMerchantsRequest request = new MerchantPlatformFetchMerchantsRequest();
        request.setMerchantIds(merchantIds);

        return InternalApiExecutor.execute(
                context,
                () -> merchantPlatformInternalClient.fetchMerchantsByIds(request)
        ).stream().collect(Collectors.toMap(
                MerchantPlatformInternalModels.MerchantData::getId,
                merchant -> merchant.getDisplayName() != null && !merchant.getDisplayName().isBlank()
                        ? merchant.getDisplayName()
                        : merchant.getLegalName(),
                (left, right) -> left
        ));
    }

    private PageInfo validatePageContext(RequestContext context, PageContext query) {
        int pageSize = ApiRequestUtils.parseIntOrDefault(query.pageSize(), DEFAULT_PAGE_SIZE, "pageSize",
                context.requestId(), context.requestDateTime(), context.channel());
        int pageNumber = ApiRequestUtils.parseIntOrDefault(query.pageNumber(), DEFAULT_PAGE_NUMBER, "pageNumber",
                context.requestId(), context.requestDateTime(), context.channel());

        if (pageSize < 1 || pageSize > 100) {
            throw new BusinessException(context.requestId(), context.requestDateTime(), context.channel(),
                    ExceptionUtils.buildResultResponse(INVALID_INPUT_ERROR, INVALID_PAGE_SIZE));
        }
        if (pageNumber < 1) {
            throw new BusinessException(context.requestId(), context.requestDateTime(), context.channel(),
                    ExceptionUtils.buildResultResponse(INVALID_INPUT_ERROR, INVALID_PAGE_NUMBER));
        }

        return new PageInfo(pageSize, pageNumber);
    }

    private FetchTripResult toFetchRouteItem(TripFetchView trip, TripEnrichment enrichment) {
        TripAssignmentRecord assignment = enrichment.assignments().get(trip.getId());
        VehicleProfile vehicle = findVehicle(assignment, enrichment);

        return FetchTripResult.builder()
                .id(trip.getId())
                .creator(trip.getCreator())
                .tripCode(trip.getTripCode())
                .originCode(trip.getOriginCode())
                .merchantId(trip.getMerchantId())
                .merchantName(trip.getMerchantName())
                .originName(trip.getOriginName())
                .destinationCode(trip.getDestinationCode())
                .destinationName(trip.getDestinationName())
                .originProvinceId(trip.getOriginProvinceId())
                .destinationProvinceId(trip.getDestinationProvinceId())
                .originDepartmentId(trip.getOriginDepartmentId())
                .originDepartmentName(trip.getOriginDepartmentName())
                .destinationDepartmentName(trip.getDestinationDepartmentName())
                .destinationDepartmentId(trip.getDestinationDepartmentId())
                .departureTime(trip.getDepartureTime())
                .rawDepartureTime(trip.getRawDepartureTime())
                .rawDepartureDate(trip.getRawDepartureDate())
                .durationMinutes(trip.getDurationMinutes())
                .status(trip.getStatus())
                .vehicleId(assignment == null ? null : assignment.getVehicleId())
                .vehiclePlate(vehicle == null ? null : vehicle.getVehiclePlate())
                .hasFloor(vehicle != null && vehicle.isHasFloor())
                .assignedAt(assignment == null ? null : assignment.getAssignedAt())
                .routePoints(toRoutePoints(enrichment.stopsByRouteId().get(trip.getRouteId())))
                .ticketPrice(assignment.getTicketPrice())
                .availableSeat(enrichment.seatAvailable.get(trip.getId()))
                .build();
    }

    private FetchTripResult toFetchTripDetail(RouteAggregate route, TripAggregate trip, TripEnrichment enrichment) {
        TripAssignmentRecord assignment = enrichment.assignments().get(trip.getId());
        VehicleProfile vehicle = findVehicle(assignment, enrichment);

        String originDepartmentName = enrichment.departmentMap.get(route.getOriginDepartmentId()).getName();
        String destinationDepartmentName = enrichment.departmentMap.get(route.getDestinationDepartmentId()).getName();

        return FetchTripResult.builder()
                .id(trip.getId())
                .creator(route.getCreator())
                .tripCode(trip.getTripCode())
                .originName(route.getOriginName())
                .originCode(route.getOriginCode())
                .destinationName(route.getDestinationName())
                .destinationCode(route.getDestinationCode())
                .originProvinceId(route.getOriginProvinceId())
                .destinationProvinceId(route.getDestinationProvinceId())
                .originDepartmentId(route.getOriginDepartmentId())
                .originDepartmentName(originDepartmentName)
                .destinationDepartmentId(route.getDestinationDepartmentId())
                .destinationDepartmentName(destinationDepartmentName)
                .departureTime(trip.getDepartureTime())
                .rawDepartureDate(trip.getRawDepartureDate())
                .rawDepartureTime(trip.getRawDepartureTime())
                .durationMinutes(route.getDuration())
                .status(trip.getStatus() == null ? null : trip.getStatus())
                .vehicleId(assignment == null ? null : assignment.getVehicleId())
                .vehiclePlate(vehicle == null ? null : vehicle.getVehiclePlate())
                .hasFloor(vehicle != null && vehicle.isHasFloor())
                .assignedAt(assignment == null ? null : assignment.getAssignedAt())
                .routePoints(toRoutePoints(enrichment.stopsByRouteId().get(route.getId())))
                .build();
    }

    private VehicleProfile findVehicle(TripAssignmentRecord assignment, TripEnrichment enrichment) {
        return assignment == null ? null : enrichment.vehicles().get(assignment.getVehicleId());
    }

    private List<RoutePointResult> toRoutePoints(List<RouteStopPlan> stops) {
        return (stops == null ? List.<RouteStopPlan>of() : stops).stream()
                .map(this::toRoutePoint)
                .toList();
    }


    private record TripEnrichment(
            Map<String, TripAssignmentRecord> assignments,
            Map<String, Long> seatAvailable,
            Map<String, VehicleProfile> vehicles,
            Map<String, List<RouteStopPlan>> stopsByRouteId,
            Map<String, Department> departmentMap
    ) {
    }
}
