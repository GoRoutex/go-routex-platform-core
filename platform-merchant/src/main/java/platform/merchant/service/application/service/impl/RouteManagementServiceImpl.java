package platform.merchant.service.application.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.com.go.routex.identity.security.log.SystemLog;
import platform.merchant.service.application.command.route.CreateRouteCommand;
import platform.merchant.service.application.command.route.CreateRouteResult;
import platform.merchant.service.application.command.route.DeleteRouteCommand;
import platform.merchant.service.application.command.route.DeleteRouteResult;
import platform.merchant.service.application.command.route.FetchDetailRouteQuery;
import platform.merchant.service.application.command.route.FetchDetailRouteResult;
import platform.merchant.service.application.command.route.FetchRouteResult;
import platform.merchant.service.application.command.route.FetchRoutesQuery;
import platform.merchant.service.application.command.route.FetchRoutesResult;
import platform.merchant.service.application.command.route.RoutePointCommand;
import platform.merchant.service.application.command.route.RoutePointResult;
import platform.merchant.service.application.command.route.UpdateRouteCommand;
import platform.merchant.service.application.command.route.UpdateRouteResult;
import platform.merchant.service.application.service.RouteManagementService;
import platform.core.common.service.application.command.common.PagedResult;
import platform.merchant.service.domain.department.model.Department;
import platform.merchant.service.domain.department.port.DepartmentRepositoryPort;
import platform.merchant.service.domain.route.RouteStatus;
import platform.merchant.service.domain.route.model.ProvincesInformationPair;
import platform.merchant.service.domain.route.model.RouteAggregate;
import platform.merchant.service.domain.route.model.RouteStopPlan;
import platform.merchant.service.domain.route.port.RouteAggregateRepositoryPort;
import platform.merchant.service.domain.route.port.RouteProvincesLookupPort;
import platform.merchant.service.domain.route.port.RouteStopRepositoryPort;
import platform.core.common.service.domain.trip.TripStatus;
import platform.merchant.service.domain.trip.model.TripAggregate;
import platform.merchant.service.domain.trip.port.TripAggregateRepositoryPort;
import platform.core.common.service.persistence.exception.BusinessException;
import platform.core.common.service.persistence.utils.ApiRequestUtils;
import platform.core.common.service.persistence.utils.ExceptionUtils;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static platform.core.common.service.persistence.constant.ApplicationConstant.DEFAULT_PAGE_NUMBER;
import static platform.core.common.service.persistence.constant.ApplicationConstant.DEFAULT_PAGE_SIZE;
import static platform.core.common.service.persistence.constant.ErrorConstant.DEPARTMENT_NOT_FOUND;
import static platform.core.common.service.persistence.constant.ErrorConstant.INVALID_INPUT_ERROR;
import static platform.core.common.service.persistence.constant.ErrorConstant.INVALID_PAGE_NUMBER;
import static platform.core.common.service.persistence.constant.ErrorConstant.INVALID_PAGE_SIZE;
import static platform.core.common.service.persistence.constant.ErrorConstant.INVALID_STOP_ORDER;
import static platform.core.common.service.persistence.constant.ErrorConstant.RECORD_NOT_FOUND;
import static platform.core.common.service.persistence.constant.ErrorConstant.ROUTE_NOT_FOUND;
import static platform.core.common.service.persistence.constant.ErrorConstant.STOP_COORDINATES_MUST_BE_PROVIDED_TOGETHER;

@Service
@RequiredArgsConstructor
public class RouteManagementServiceImpl implements RouteManagementService {

    private final RouteAggregateRepositoryPort routeAggregateRepositoryPort;
    private final RouteStopRepositoryPort routeStopRepositoryPort;
    private final RouteProvincesLookupPort routeProvincesLookupPort;
    private final DepartmentRepositoryPort departmentRepositoryPort;

    private final SystemLog sLog = SystemLog.getLogger(this.getClass());
    private final TripAggregateRepositoryPort tripAggregateRepositoryPort;

    @Override
    @Transactional
    public CreateRouteResult createRoute(CreateRouteCommand command) {
        String originName = command.originName().trim();
        String destinationName = command.destinationName().trim();

        String originProvinceId = null;
        String destinationProvinceId = null;

        String originDepartmentName = null;
        String destinationDepartmentName = null;

        if (hasText(command.originDepartmentId())) {
            Department originDepartment = departmentRepositoryPort.findById(command.originDepartmentId(), command.merchantId())
                    .orElse(null);

            if(originDepartment != null) {
                originProvinceId = originDepartment.getProvinceId();
                originDepartmentName = originDepartment.getName();
            }
        }

        if (hasText(command.destinationDepartmentId())) {
            Department destinationDepartment = departmentRepositoryPort.findById(command.destinationDepartmentId(), command.merchantId())
                    .orElse(null);

            if(destinationDepartment != null) {
                destinationProvinceId = destinationDepartment.getProvinceId();
                destinationDepartmentName = destinationDepartment.getName();
            }
        }

        ProvincesInformationPair codeResult = routeProvincesLookupPort.getCodes(command.originName(), command.destinationName());

        String originCode = codeResult.originCode();
        String destinationCode = codeResult.destinationCode();

        List<RoutePointCommand> routePoints =
                Optional.ofNullable(command.routePoints())
                        .orElseGet(List::of);

        // Validate stop points
        validateRoutePoints(command);

        List<String> departmentIds = routePoints.stream()
                .map(RoutePointCommand::departmentId)
                .toList();


        List<Department> departmentList = departmentRepositoryPort.findAllByIdIn(departmentIds);

        Map<String, Department> departmentMap = departmentList.stream()
                .collect(Collectors.toMap(
                        Department::getId,
                        Function.identity()
                ));

        OffsetDateTime now = OffsetDateTime.now();
        String routeId = UUID.randomUUID().toString();
        List<RouteStopPlan> routeStopPlans = routePoints.stream()
                .map(point -> {
                    Department department = departmentMap.get(point.departmentId());
                    return RouteStopPlan.builder()
                            .id(UUID.randomUUID().toString())
                            .routeId(routeId)
                            .stopOrder(Integer.parseInt(point.stopOrder()))
                            .creator(command.creator())
                            .createdAt(now)
                            .createdBy(command.creator())
                            .note(department.getNote())
                            .departmentId(point.departmentId())
                            .stopName(department.getName())
                            .stopAddress(department.getAddress())
                            .stopCity(department.getProvinceName())
                            .stopLatitude(department.getLatitude())
                            .stopLongitude(department.getLongitude())
                            .timeAtDepartment(point.timeAtDepartment())
                            .build();
                })
                .collect(toList());

        RouteAggregate newRoute = RouteAggregate.plan(
                routeId,
                command.creator(),
                command.merchantId(),
                originCode,
                destinationCode,
                originProvinceId,
                destinationProvinceId,
                command.originDepartmentId(),
                originDepartmentName,
                destinationDepartmentName,
                command.destinationDepartmentId(),
                originName,
                destinationName,
                command.duration(),
                0L,
                now,
                routeStopPlans
        );

        sLog.info("Route stop plans: {}",  routeStopPlans);

        routeAggregateRepositoryPort.save(newRoute);
        routeStopRepositoryPort.saveAll(routeStopPlans);

        return CreateRouteResult.builder()
                .id(newRoute.getId())
                .creator(command.creator())
                .originCode(originCode)
                .originName(command.originName())
                .destinationCode(destinationCode)
                .destinationName(destinationName)
                .originProvinceId(originProvinceId)
                .destinationProvinceId(destinationProvinceId)
                .originDepartmentId(command.originDepartmentId())
                .originDepartmentName(originDepartmentName)
                .destinationDepartmentId(command.destinationDepartmentId())
                .destinationDepartmentName(destinationDepartmentName)
                .status(RouteStatus.ACTIVE)
                .duration(command.duration())
                .routePoints(command.routePoints() != null ?
                        command.routePoints() : null)
                .build();
    }

    @Override
    @Transactional
    public UpdateRouteResult updateRoute(UpdateRouteCommand command) {
        RouteAggregate route = routeAggregateRepositoryPort.findById(command.routeId(), command.context().merchantId())
                .orElseThrow(() -> new BusinessException(command.context().requestId(), command.context().requestDateTime(), command.context().channel(),
                        ExceptionUtils.buildResultResponse(RECORD_NOT_FOUND, String.format(ROUTE_NOT_FOUND, command.routeId()))));

        processRoutePoints(command);

        Optional<ProvincesInformationPair> codeResult = Optional.ofNullable(command.originName())
                .flatMap(origin -> Optional.ofNullable(command.destinationName())
                        .map(dest -> routeProvincesLookupPort.getCodes(origin, dest)));

        String originCode = codeResult.map(ProvincesInformationPair::originCode).orElse(null);
        String destinationCode = codeResult.map(ProvincesInformationPair::destinationCode).orElse(null);


        Optional.ofNullable(command.originDepartmentId())
                .ifPresent(route::setOriginDepartmentId);

        if(command.originDepartmentId() != null) {
            departmentRepositoryPort.findById(command.originDepartmentId())
                    .ifPresent(originDepartment -> route.setOriginDepartmentName(originDepartment.getName()));

        }

        if(command.destinationDepartmentId() != null) {
            departmentRepositoryPort.findById(command.destinationDepartmentId())
                    .ifPresent(destinationDepartment -> route.setDestinationDepartmentName(destinationDepartment.getName()));
        }

        Optional.ofNullable(command.destinationDepartmentId())
                .ifPresent(route::setDestinationDepartmentId);

        Optional.ofNullable(command.duration())
                .ifPresent(route::setDuration);

        Optional.ofNullable(originCode)
                .ifPresent(route::setOriginCode);

        Optional.ofNullable(destinationCode)
                .ifPresent(route::setDestinationCode);

        Optional.ofNullable(command.originName())
                .ifPresent(route::setOriginName);

        Optional.ofNullable(command.destinationName())
                .ifPresent(route::setDestinationName);

        routeAggregateRepositoryPort.save(route);

        return toUpdateRouteResult(command, originCode, destinationCode);
    }

    @Override
    @Transactional
    public DeleteRouteResult deleteRoute(DeleteRouteCommand command) {
        RouteAggregate route = routeAggregateRepositoryPort.findById(command.routeId(), command.merchantId())
                .orElseThrow(() -> new BusinessException(command.context().requestId(), command.context().requestDateTime(), command.context().channel(),
                        ExceptionUtils.buildResultResponse(RECORD_NOT_FOUND, String.format(ROUTE_NOT_FOUND, command.routeId()))));

        Optional<TripAggregate> optTrip = tripAggregateRepositoryPort.findByRouteId(route.getId(), command.merchantId());
        OffsetDateTime now = OffsetDateTime.now();
        if(optTrip.isPresent()) {
            TripAggregate trip = optTrip.get();
            trip.setStatus(TripStatus.CANCELLED);
            trip.setUpdatedAt(now);
            tripAggregateRepositoryPort.save(trip);
        }
        route.cancel(command.creator(), now);
        routeAggregateRepositoryPort.save(route);

        return DeleteRouteResult.builder()
                .creator(command.creator())
                .routeId(route.getId())
                .status(route.getStatus().name())
                .updatedAt(route.getUpdatedAt())
                .build();
    }

    @Override
    public FetchRoutesResult fetchRoutes(FetchRoutesQuery query) {

        int pageSize = ApiRequestUtils.parseIntOrDefault(query.pageSize(), DEFAULT_PAGE_SIZE, "pageSize",
                query.context().requestId(), query.context().requestDateTime(), query.context().channel());
        int pageNumber = ApiRequestUtils.parseIntOrDefault(query.pageNumber(), DEFAULT_PAGE_NUMBER, "pageNumber",
                query.context().requestId(), query.context().requestDateTime(), query.context().channel());

        validatePaging(query, pageSize, pageNumber);

        PagedResult<RouteAggregate> page;
        if(query.status() != null) {
            page = routeAggregateRepositoryPort.fetch(query.context().merchantId(), query.status(),pageNumber - 1, pageSize);
        } else {
            page = routeAggregateRepositoryPort.fetch(query.context().merchantId(), pageNumber - 1, pageSize);
        }

        List<RouteAggregate> listRoutes = page.getItems();

        List<String> departmentIds = listRoutes
                .stream()
                .flatMap(route -> Stream.of(
                        route.getOriginDepartmentId(),
                        route.getDestinationDepartmentId()
                ))
                .filter(Objects::nonNull)
                .distinct().toList();

        List<Department> departmentList = departmentRepositoryPort.findAllByIdIn(departmentIds);

        Map<String, Department> departmentMap = departmentList
                .stream()
                .collect(Collectors.toMap(
                        Department::getId,
                        Function.identity()
                ));

        sLog.info("Department Map: {}", departmentMap);

        return FetchRoutesResult.builder()
                .items(page.getItems().stream()
                        .map(result -> toFetchDetailResult(result, departmentMap))
                        .toList())
                .pageNumber(page.getPageNumber() + 1)
                .pageSize(page.getPageSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
    }

    @Override
    public FetchDetailRouteResult fetchDetailRoute(FetchDetailRouteQuery query) {

        sLog.info("[ROUTE-DETAIL] Fetch Detail Query: {}", query);

        RouteAggregate routeAggregate = routeAggregateRepositoryPort.findById(query.routeId(), query.merchantId())
                .orElseThrow(() -> new BusinessException(query.context().requestId(), query.context().requestDateTime(), query.context().channel(),
                        ExceptionUtils.buildResultResponse(RECORD_NOT_FOUND, String.format(ROUTE_NOT_FOUND, query.routeId()))));

        List<RouteStopPlan> routeStopPlans = routeStopRepositoryPort.findByRouteId(query.routeId());

        List<RoutePointResult> routePointResults = routeStopPlans.isEmpty() ? null :
                routeStopPlans.stream()
                        .map(this::toRoutePointResult)
                        .toList();

        List<String> departmentIds = List.of(routeAggregate.getOriginDepartmentId(), routeAggregate.getDestinationDepartmentId());

        List<Department> departmentList = departmentRepositoryPort.findAllByIdIn(departmentIds);

        Map<String, Department> departmentMap = departmentList
                .stream()
                .collect(Collectors.toMap(
                        Department::getId,
                        Function.identity()
                ));


        String originDepartmentName = departmentMap.get(routeAggregate.getOriginDepartmentId()).getName();
        String destinationDepartmentName = departmentMap.get(routeAggregate.getDestinationDepartmentId()).getName();

        return FetchDetailRouteResult.builder()
                .id(routeAggregate.getId())
                .creator(routeAggregate.getCreator())
                .originCode(routeAggregate.getOriginCode())
                .originName(routeAggregate.getOriginName())
                .destinationCode(routeAggregate.getDestinationCode())
                .destinationName(routeAggregate.getDestinationName())
                .originProvinceId(routeAggregate.getOriginProvinceId())
                .destinationProvinceId(routeAggregate.getDestinationProvinceId())
                .originDepartmentId(routeAggregate.getOriginDepartmentId())
                .originDepartmentName(originDepartmentName)
                .destinationDepartmentId(routeAggregate.getDestinationDepartmentId())
                .destinationDepartmentName(destinationDepartmentName)
                .status(routeAggregate.getStatus())
                .duration(routeAggregate.getDuration())
                .routePoints(routePointResults)
                .build();
    }

    private RoutePointResult toRoutePointResult(RouteStopPlan stop) {
        return RoutePointResult.builder()
                .id(stop.getId())
                .routeId(stop.getRouteId())
                .creator(stop.getCreator())
                .stopOrder(stop.getStopOrder())
                .note(stop.getNote())
                .departmentId(stop.getDepartmentId())
                .stopName(stop.getStopName())
                .stopAddress(stop.getStopAddress())
                .stopCity(stop.getStopCity())
                .stopLatitude(stop.getStopLatitude())
                .stopLongitude(stop.getStopLongitude())
                .stayDuration(stop.getStayDuration())
                .timeAtDepartment(stop.getTimeAtDepartment())
                .createdAt(stop.getCreatedAt())
                .createdBy(stop.getCreatedBy())
                .build();
    }
    private FetchRouteResult toFetchDetailResult(RouteAggregate aggregate, Map<String, Department> departmentMap) {

        String originDepartmentName = departmentMap.get(aggregate.getOriginDepartmentId()).getName();
        String destinationDepartmentName = departmentMap.get(aggregate.getDestinationDepartmentId()).getName();

        return FetchRouteResult.builder()
                .id(aggregate.getId())
                .creator(aggregate.getCreator())
                .originCode(aggregate.getOriginCode())
                .originName(aggregate.getOriginName())
                .destinationCode(aggregate.getDestinationCode())
                .destinationName(aggregate.getDestinationName())
                .originDepartmentId(aggregate.getOriginDepartmentId())
                .originDepartmentName(originDepartmentName)
                .destinationDepartmentId(aggregate.getDestinationDepartmentId())
                .destinationDepartmentName(destinationDepartmentName)
                .duration(aggregate.getDuration())
                .status(aggregate.getStatus())
                .routePoints(
                        aggregate.getStopPlans() != null ? aggregate.getStopPlans().stream()
                                .map(this::toRoutePointResult)
                                .toList() : null)
                .build();
    }

    private UpdateRouteResult toUpdateRouteResult(UpdateRouteCommand command, String originCode, String destinationCode) {
        List<UpdateRouteResult.UpdateRoutePointResult> routePointResults = command.routePoints() != null ?
                command.routePoints().stream()
                        .map(point -> UpdateRouteResult.UpdateRoutePointResult.builder()
                                .stopOrder(point.stopOrder())
                                .note(point.note())
                                .departmentId(point.departmentId())
                                .stopName(point.stopName())
                                .stopAddress(point.stopAddress())
                                .stopCity(point.stopCity())
                                .stopLatitude(point.stopLatitude())
                                .stopLongitude(point.stopLongitude())
                                .timeAtDepartment(point.timeAtDepartment())
                                .build())
                        .toList() : null;

        return UpdateRouteResult.builder()
                .routeId(command.routeId())
                .creator(command.creator())
                .originCode(originCode)
                .originName(command.originName())
                .originDepartmentId(command.originDepartmentId())
                .destinationCode(destinationCode)
                .destinationName(command.destinationName())
                .destinationDepartmentId(command.destinationDepartmentId())
                .status(command.status())
                .routePoints(routePointResults)
                .duration(command.duration())
                .build();
    }
    private void validatePaging(FetchRoutesQuery query, int pageSize, int pageNumber) {
        if (pageSize < 1 || pageSize > 100) {
            throw new BusinessException(query.context().requestId(), query.context().requestDateTime(), query.context().channel(),
                    ExceptionUtils.buildResultResponse(INVALID_INPUT_ERROR, INVALID_PAGE_SIZE));
        }
        if (pageNumber < 1) {
            throw new BusinessException(query.context().requestId(), query.context().requestDateTime(), query.context().channel(),
                    ExceptionUtils.buildResultResponse(INVALID_INPUT_ERROR, INVALID_PAGE_NUMBER));
        }
    }
    private void validateRoutePoints(CreateRouteCommand command) {
        List<RoutePointCommand> routePoints = command.routePoints();
        if (routePoints == null || routePoints.isEmpty()) {
            return;
        }

        Set<Integer> setOfOrders = new HashSet<>();

        for (RoutePointCommand point : routePoints) {
            validateRoutePoint(command, point, setOfOrders);
        }
    }

    private void validateRoutePoint(CreateRouteCommand command, RoutePointCommand point, Set<Integer> setOfOrders) {
        Integer operationOrder = validateStopOrder(command, point);
        if (!setOfOrders.add(operationOrder)) {
            throwInvalidInput(command, INVALID_STOP_ORDER);
        }

        boolean hasDepartmentId = hasText(point.departmentId());

        if (hasDepartmentId) {
            validateDepartment(command, point.departmentId().trim());
            return;
        }

        validateCustomStopCoordinates(command, point);
    }


    private void processRoutePoints(UpdateRouteCommand command) {
        if (command.routePoints() == null || command.routePoints().isEmpty()) {
            return;
        }
        List<String> departmentIds = command.routePoints()
                .stream()
                .map(UpdateRouteCommand.UpdateRoutePointCommand::departmentId)
                .distinct()
                .filter(Objects::nonNull)
                .toList();

        Map<String, Department> departmentMap = departmentIds.isEmpty() ? Collections.emptyMap() :
                departmentRepositoryPort.findAllByIdIn(departmentIds)
                        .stream()
                        .collect(Collectors.toMap(
                                Department::getId,
                                dept -> dept
                        ));

        for(UpdateRouteCommand.UpdateRoutePointCommand point : command.routePoints()) {
            RouteStopPlan routeStopPlan = routeStopRepositoryPort.findByRouteIdAndStopOrder(command.routeId(), String.valueOf(point.stopOrder()))
                    .orElse(null);

            if(routeStopPlan != null) {
                Optional.ofNullable(point.note())
                        .ifPresent(routeStopPlan::setNote);
                Optional.ofNullable(point.timeAtDepartment())
                        .ifPresent(routeStopPlan::setTimeAtDepartment);
            } else {
                routeStopPlan = RouteStopPlan.builder()
                        .id(UUID.randomUUID().toString())
                        .stopOrder(point.stopOrder())
                        .creator(command.creator())
                        .routeId(command.routeId())
                        .note(point.note())
                        .departmentId(point.departmentId())
                        .timeAtDepartment(point.timeAtDepartment())
                        .build();
            }

            if(point.departmentId() != null) {
                Department department = departmentMap.get(point.departmentId());

                if(department == null) {
                    throw new BusinessException(command.context().requestId(), command.context().requestDateTime(), command.context().channel(),
                            ExceptionUtils.buildResultResponse(RECORD_NOT_FOUND, String.format(DEPARTMENT_NOT_FOUND, point.departmentId())));
                }

                routeStopPlan.setDepartmentId(point.departmentId());
                routeStopPlan.setStopName(department.getName());
                routeStopPlan.setStopAddress(department.getAddress());
                routeStopPlan.setStopCity(department.getProvinceName());
                routeStopPlan.setStopLatitude(department.getLatitude());
                routeStopPlan.setStopLongitude(department.getLongitude());
            }
            routeStopRepositoryPort.save(routeStopPlan);
        }
    }
    private Integer validateStopOrder(CreateRouteCommand command, RoutePointCommand point) {
        if (point.stopOrder() == null) {
            throwInvalidInput(command, INVALID_STOP_ORDER);
        }

        try {
            int operationOrder = Integer.parseInt(point.stopOrder());
            if (operationOrder <= 0) {
                throwInvalidInput(command, INVALID_STOP_ORDER);
            }
            return operationOrder;
        } catch (NumberFormatException exception) {
            throwInvalidInput(command, INVALID_STOP_ORDER);
            return null;
        }
    }

    private void validateDepartment(CreateRouteCommand command, String departmentId) {
        departmentRepositoryPort.findById(departmentId, command.merchantId())
                .orElseThrow(() -> new BusinessException(
                        command.context().requestId(),
                        command.context().requestDateTime(),
                        command.context().channel(),
                        ExceptionUtils.buildResultResponse(
                                RECORD_NOT_FOUND,
                                String.format(DEPARTMENT_NOT_FOUND, departmentId))));
    }

    private void validateCustomStopCoordinates(CreateRouteCommand command, RoutePointCommand point) {
        if (point.stopLatitude() != null ^ point.stopLongitude() != null) {
            throwInvalidInput(command, STOP_COORDINATES_MUST_BE_PROVIDED_TOGETHER);
        }
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private void throwInvalidInput(CreateRouteCommand command, String message) {
        throw new BusinessException(
                command.context().requestId(),
                command.context().requestDateTime(),
                command.context().channel(),
                ExceptionUtils.buildResultResponse(INVALID_INPUT_ERROR, message));
    }
}
