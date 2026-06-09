package platform.management.service.application.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import platform.core.common.service.application.command.common.PageContext;
import platform.core.common.service.application.command.common.PageInfo;
import platform.core.common.service.application.command.common.PagedResult;
import platform.core.common.service.common.RequestContext;
import platform.core.common.service.domain.seat.port.TripSeatAvailabilityPort;
import platform.core.common.service.persistence.exception.BusinessException;
import platform.core.common.service.persistence.utils.ExceptionUtils;
import platform.management.service.application.command.route.FetchTripQuery;
import platform.management.service.application.command.route.FetchTripResult;
import platform.management.service.application.command.route.FetchTripsQuery;
import platform.management.service.application.command.route.FetchTripsResult;
import platform.management.service.application.command.route.RoutePointResult;
import platform.management.service.application.command.route.SearchTripItemResult;
import platform.management.service.application.command.route.SearchTripQuery;
import platform.management.service.application.command.route.SearchTripResult;
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
import platform.merchant.service.domain.trip.port.TripQueryPort;
import platform.merchant.service.domain.trip.readmodel.TripFetchView;
import platform.merchant.service.domain.vehicle.model.VehicleProfile;
import platform.merchant.service.domain.vehicle.port.VehicleProfileRepositoryPort;
import vn.com.go.routex.identity.security.log.SystemLog;

import java.util.List;
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
    public SearchTripResult searchTrip(SearchTripQuery query) {
        PageInfo pageInfo = validatePageContext(query.context(), query.pageContext());

        List<platform.core.common.service.application.readmodel.TripSearchView> searchedRoutes = tripQueryPort.searchAssignedTrips(
                null,
                query.originName(),
                query.destinationName(),
                pageInfo.pageNumber() - 1, // external is 1-based; Spring Data is 0-based
                pageInfo.pageSize()
        );

        TripEnrichment enrichment = enrichRoutes(
                searchedRoutes.stream().map(platform.core.common.service.application.readmodel.TripSearchView::getId).toList(),
                searchedRoutes.stream().map(platform.core.common.service.application.readmodel.TripSearchView::getRouteId).toList(),
                searchedRoutes.stream().flatMap(route -> Stream.of(
                        route.getOriginDepartmentId(),
                        route.getDestinationDepartmentId()
                ))
                        .filter(Objects::nonNull)
                        .distinct().toList()
        );

        sLog.info("Enrichment: {}", enrichment);

        Map<String, String> merchantNames = fetchMerchantNames(
                searchedRoutes.stream().map(platform.core.common.service.application.readmodel.TripSearchView::getMerchantId).distinct().toList(),
                query.context()
        );

        List<SearchTripItemResult> items = searchedRoutes.stream()
                .map(route -> toSearchRouteItem(route, enrichment, merchantNames))
                .collect(Collectors.toList());

        return SearchTripResult.builder()
                .data(items)
                .build();
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


    @Override
    public FetchTripsResult fetchTrips(FetchTripsQuery query) {
        PageInfo pageInfo = validatePageContext(query.context(), query.pageContext());
        // external is 1-based; Spring Data is 0-based
        PagedResult<TripFetchView> page = tripQueryPort.fetchTrips(
                query.merchantId(),
                resolveMerchantIds(query),
                pageInfo.pageNumber() - 1,
                pageInfo.pageSize()
        );

        List<TripFetchView> routes = page.getItems();
        TripEnrichment enrichment = enrichRoutes(
                routes.stream().map(TripFetchView::getId).toList(),
                routes.stream().map(TripFetchView::getRouteId).toList(),
                routes.stream()
                        .flatMap(route -> Stream.of(
                                route.getOriginDepartmentId(),
                                route.getDestinationDepartmentId()
                        ))
                        .filter(Objects::nonNull)
                        .distinct()
                        .toList()
        );

        List<FetchTripResult> items = routes.stream()
                .map(route -> toFetchRouteItem(route, enrichment))
                .collect(Collectors.toList());

        return FetchTripsResult.builder()
                .items(items)
                .pageNumber(page.getPageNumber() + 1)
                .pageSize(page.getPageSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
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
            platform.core.common.service.application.readmodel.TripSearchView trip,
            TripEnrichment enrichment,
            Map<String, String> merchantNames
    ) {
        TripAssignmentRecord assignment = enrichment.assignments().get(trip.getId());
        VehicleProfile vehicle = findVehicle(assignment, enrichment);
        List<RouteStopPlan> routeStopPlans = enrichment.stopsByRouteId().get(trip.getRouteId());
        List<RoutePointResult> routePointResults = toRoutePoints(routeStopPlans);

        String originDepartmentName = enrichment.departmentMap.get(trip.getOriginDepartmentId()).getName();
        String destinationDepartmentName = enrichment.departmentMap.get(trip.getDestinationDepartmentId()).getName();

        return SearchTripItemResult.builder()
                .id(trip.getId())
                .merchantId(trip.getMerchantId())
                .vehicleId(assignment.getVehicleId())
                .driverId(assignment.getDriverId())
                .ticketPrice(assignment.getTicketPrice())
                .merchantName(merchantNames.get(trip.getMerchantId()))
                .tripCode(trip.getTripCode())
                .originCode(trip.getOriginCode())
                .originName(trip.getOriginName())
                .destinationName(trip.getDestinationName())
                .destinationCode(trip.getDestinationCode())
                .originProvinceId(trip.getOriginProvinceId())
                .destinationProvinceId(trip.getDestinationProvinceId())
                .originDepartmentId(trip.getOriginDepartmentId())
                .originDepartmentName(originDepartmentName)
                .destinationDepartmentId(trip.getDestinationDepartmentId())
                .destinationDepartmentName(destinationDepartmentName)
                .availableSeats(enrichment.seatAvailable().getOrDefault(trip.getId(), 0L))
                .departureTime(trip.getDepartureTime())
                .rawDepartureTime(trip.getRawDepartureTime())
                .rawDepartureDate(trip.getRawDepartureDate())
                .durationMinutes(trip.getDurationMinutes())
                .vehiclePlate(vehicle == null ? null : vehicle.getVehiclePlate())
                .hasFloor(vehicle != null && vehicle.isHasFloor())
                .routePoints(routePointResults)
                .build();
    }

    private List<String> resolveMerchantIds(FetchTripsQuery query) {
        if (query.merchantName() == null || query.merchantName().isBlank()) {
            return null;
        }

        return InternalApiExecutor.execute(
                query.context(),
                () -> merchantPlatformInternalClient.searchMerchantIds(query.merchantName().trim())
        );
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
                .originName(trip.getOriginName())
                .destinationCode(trip.getDestinationCode())
                .destinationName(trip.getDestinationName())
                .originProvinceId(trip.getOriginProvinceId())
                .destinationProvinceId(trip.getDestinationProvinceId())
                .originDepartmentId(trip.getOriginDepartmentId())
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
