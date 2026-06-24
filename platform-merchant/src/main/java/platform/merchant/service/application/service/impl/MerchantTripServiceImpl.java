package platform.merchant.service.application.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;
import platform.core.common.service.application.command.common.PagedResult;
import platform.core.common.service.application.service.EntityPartitionService;
import platform.core.common.service.application.service.OutBoxService;
import platform.core.common.service.domain.trip.TripStatus;
import platform.core.common.service.domain.vehicle.VehicleTemplateStatus;
import platform.core.common.service.infrastructure.kafka.activity.RecentActivityPublisher;
import platform.core.common.service.infrastructure.kafka.event.AiOptimizationRequestedEvent;
import platform.core.common.service.infrastructure.kafka.event.TripSellableEvent;
import platform.core.common.service.persistence.exception.BusinessException;
import platform.core.common.service.persistence.utils.ApiRequestUtils;
import platform.core.common.service.persistence.utils.ExceptionUtils;
import platform.merchant.service.application.command.route.AssignRouteBatchCommand;
import platform.merchant.service.application.command.route.AssignRouteBatchFailedItem;
import platform.merchant.service.application.command.route.AssignRouteBatchResult;
import platform.merchant.service.application.command.route.AssignRouteCommand;
import platform.merchant.service.application.command.route.AssignRouteResult;
import platform.merchant.service.application.command.trip.CreateTripBatchCommand;
import platform.merchant.service.application.command.trip.CreateTripBatchResult;
import platform.merchant.service.application.command.trip.CreateTripCommand;
import platform.merchant.service.application.command.trip.CreateTripResult;
import platform.merchant.service.application.command.trip.DeleteTripCommand;
import platform.merchant.service.application.command.trip.DeleteTripResult;
import platform.merchant.service.application.command.trip.FetchTripDetailQuery;
import platform.merchant.service.application.command.trip.FetchTripDetailResult;
import platform.merchant.service.application.command.trip.FetchTripListQuery;
import platform.merchant.service.application.command.trip.FetchTripListResult;
import platform.merchant.service.application.command.trip.ScheduleAsyncCommand;
import platform.merchant.service.application.command.trip.ScheduleAsyncResult;
import platform.merchant.service.application.command.trip.UpdateTripCommand;
import platform.merchant.service.application.command.trip.UpdateTripResult;
import platform.merchant.service.application.service.HolidayService;
import platform.merchant.service.application.service.MerchantTripService;
import platform.merchant.service.domain.assignment.model.TripAssignmentRecord;
import platform.merchant.service.domain.assignment.port.TripAssignmentRepositoryPort;
import platform.merchant.service.domain.driver.model.DriverProfile;
import platform.merchant.service.domain.driver.port.DriverProfileRepositoryPort;
import platform.merchant.service.domain.job.OptimizationJobStatus;
import platform.merchant.service.domain.route.model.RouteAggregate;
import platform.merchant.service.domain.route.port.RouteAggregateRepositoryPort;
import platform.merchant.service.domain.trip.model.TripAggregate;
import platform.merchant.service.domain.trip.port.TripAggregateRepositoryPort;
import platform.merchant.service.domain.vehicle.model.VehicleProfile;
import platform.merchant.service.domain.vehicle.model.VehicleTemplate;
import platform.merchant.service.domain.vehicle.port.VehicleProfileRepositoryPort;
import platform.merchant.service.domain.vehicle.port.VehicleTemplateRepositoryPort;
import platform.merchant.service.infrastructure.persistence.jpa.job.entity.OptimizationJobEntity;
import platform.merchant.service.infrastructure.persistence.jpa.job.repository.OptimizationJobRepository;
import vn.com.go.routex.identity.security.log.SystemLog;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import static platform.core.common.service.persistence.constant.ApplicationConstant.DEFAULT_PAGE_NUMBER;
import static platform.core.common.service.persistence.constant.ApplicationConstant.DEFAULT_PAGE_SIZE;
import static platform.core.common.service.persistence.constant.ApplicationConstant.DEFAULT_ZONE;
import static platform.core.common.service.persistence.constant.ErrorConstant.DRIVER_NOT_FOUND_MESSAGE;
import static platform.core.common.service.persistence.constant.ErrorConstant.DUPLICATE_ERROR;
import static platform.core.common.service.persistence.constant.ErrorConstant.DUPLICATE_ROUTE_ASSIGNMENT;
import static platform.core.common.service.persistence.constant.ErrorConstant.INVALID_INPUT_ERROR;
import static platform.core.common.service.persistence.constant.ErrorConstant.INVALID_PAGE_NUMBER;
import static platform.core.common.service.persistence.constant.ErrorConstant.INVALID_PAGE_SIZE;
import static platform.core.common.service.persistence.constant.ErrorConstant.RECORD_NOT_FOUND;
import static platform.core.common.service.persistence.constant.ErrorConstant.ROUTE_NOT_FOUND;
import static platform.core.common.service.persistence.constant.ErrorConstant.TRIP_ALREADY_EXISTS_FOR_ROUTE;
import static platform.core.common.service.persistence.constant.ErrorConstant.TRIP_NOT_FOUND;
import static platform.core.common.service.persistence.constant.ErrorConstant.VEHICLE_NOT_FOUND;
import static platform.core.common.service.persistence.constant.ErrorConstant.VEHICLE_TEMPLATE_NOT_FOUND;


@Service
@RequiredArgsConstructor
public class MerchantTripServiceImpl implements MerchantTripService {

    private static final String PERIOD_MONTH = "MONTH";
    private static final String PERIOD_QUARTER = "QUARTER";
    private static final String PERIOD_YEAR = "YEAR";

    private final TripAggregateRepositoryPort tripAggregateRepositoryPort;
    private final RouteAggregateRepositoryPort routeAggregateRepositoryPort;
    private final TripAssignmentRepositoryPort tripAssignmentRepositoryPort;
    private final VehicleProfileRepositoryPort vehicleProfileRepositoryPort;
    private final VehicleTemplateRepositoryPort vehicleTemplateRepositoryPort;
    private final DriverProfileRepositoryPort driverProfileRepositoryPort;
    private final OutBoxService outBoxService;
    private final HolidayService holidayService;
    private final PlatformTransactionManager transactionManager;
    private final OptimizationJobRepository optimizationJobRepository;
    private final RecentActivityPublisher recentActivityPublisher;
    private final EntityPartitionService entityPartitionService;

    @Value("${spring.kafka.topics.trips}")
    private String tripTopic;

    @Value("${spring.kafka.events.trip-assigned}")
    private String tripAssignedEvent;

    @Value("${spring.kafka.events.trip-ready-for-sale}")
    private String tripReadyForSaleEvent;

    private final SystemLog sLog = SystemLog.getLogger(this.getClass());

    @Override
    @Transactional
    public CreateTripResult createTrip(CreateTripCommand command) {
        RouteAggregate route = findRoute(command.routeId(), command.merchantId(), command.context().requestId(),
                command.context().requestDateTime(), command.context().channel());

        String tripCode = tripAggregateRepositoryPort.generateTripCode(route.getOriginCode(), route.getDestinationCode());
        OffsetDateTime now = OffsetDateTime.now();
        TripAggregate trip = TripAggregate.builder()
                .id(UUID.randomUUID().toString())
                .routeId(route.getId())
                .merchantId(command.merchantId())
                .tripCode(tripCode)
                .departureTime(command.departureTime())
                .rawDepartureTime(command.rawDepartureTime())
                .rawDepartureDate(command.rawDepartureDate())
                .status(TripStatus.SCHEDULED)
                .createdAt(now)
                .createdBy(command.creator())
                .updatedAt(now)
                .updatedBy(command.creator())
                .build();

        entityPartitionService.ensureTripPartition(trip.getDepartureTime());
        tripAggregateRepositoryPort.save(trip);
        return toCreateResult(trip);
    }

    @Override
    @Transactional
    public CreateTripBatchResult createTripBatch(CreateTripBatchCommand command) {
        RouteAggregate route = findRoute(command.routeId(), command.merchantId(), command.context().requestId(),
                command.context().requestDateTime(), command.context().channel());

        String tripCodeBase = tripAggregateRepositoryPort.generateTripCode(route.getOriginCode(), route.getDestinationCode());
        OffsetDateTime now = OffsetDateTime.now();

        List<String> tripIds = new ArrayList<>();
        List<TripAggregate> tripsToSave = new ArrayList<>();

        for (int i = 0; i < command.trips().size(); i++) {
            CreateTripBatchCommand.TripBatchCommandData tripData = command.trips().get(i);
            String tripId = UUID.randomUUID().toString();
            TripAggregate trip = TripAggregate.builder()
                    .id(tripId)
                    .routeId(route.getId())
                    .merchantId(command.merchantId())
                    .tripCode(tripCodeBase + "-" + (i + 1))
                    .departureTime(tripData.departureTime())
                    .rawDepartureTime(tripData.rawDepartureTime())
                    .rawDepartureDate(tripData.rawDepartureDate())
                    .status(TripStatus.SCHEDULED)
                    .createdAt(now)
                    .createdBy(command.merchantId())
                    .updatedAt(now)
                    .updatedBy(command.merchantId())
                    .build();
            tripsToSave.add(trip);
            tripIds.add(tripId);
        }

        tripsToSave.stream()
                .map(TripAggregate::getDepartureTime)
                .filter(Objects::nonNull)
                .distinct()
                .forEach(entityPartitionService::ensureTripPartition);

        sLog.info("Batch saving {} trips for route {}", tripsToSave.size(), command.routeId());
        tripAggregateRepositoryPort.saveAll(tripsToSave);

        return CreateTripBatchResult.builder()
                .routeId(command.routeId())
                .tripIds(tripIds)
                .build();
    }


    @Override
    @Transactional
    public UpdateTripResult updateTrip(UpdateTripCommand command) {
        TripAggregate existing = findTrip(command.tripId(), command.merchantId(), command.context().requestId(),
                command.context().requestDateTime(), command.context().channel());

        if (command.routeId() != null && !command.routeId().isBlank() && !command.routeId().equals(existing.getRouteId())) {
            findRoute(command.routeId(), command.merchantId(), command.context().requestId(),
                    command.context().requestDateTime(), command.context().channel());

            if (tripAggregateRepositoryPort.existsByRouteId(command.routeId().trim(), command.merchantId())) {
                throw new BusinessException(command.context().requestId(), command.context().requestDateTime(), command.context().channel(),
                        ExceptionUtils.buildResultResponse(DUPLICATE_ERROR, String.format(TRIP_ALREADY_EXISTS_FOR_ROUTE, command.routeId())));
            }
        }

        TripAggregate updated = existing.toBuilder()
                .routeId(ApiRequestUtils.firstNonBlank(command.routeId(), existing.getRouteId()))
                .merchantId(existing.getMerchantId())
                .tripCode(existing.getTripCode())
                .departureTime(command.departureTime() == null ? existing.getDepartureTime() : command.departureTime())
                .rawDepartureTime(ApiRequestUtils.firstNonBlank(command.rawDepartureTime(), existing.getRawDepartureTime()))
                .rawDepartureDate(ApiRequestUtils.firstNonBlank(command.rawDepartureDate(), existing.getRawDepartureDate()))
                .status(existing.getStatus())
                .updatedAt(OffsetDateTime.now())
                .updatedBy(command.merchantId())
                .build();

        entityPartitionService.ensureTripPartition(updated.getDepartureTime());
        tripAggregateRepositoryPort.save(updated);
        return toUpdateResult(updated);
    }

    @Override
    @Transactional
    public DeleteTripResult deleteTrip(DeleteTripCommand command) {
        TripAggregate existing = findTrip(command.tripId(), command.merchantId(), command.context().requestId(),
                command.context().requestDateTime(), command.context().channel());

        TripAggregate cancelled = existing.toBuilder()
                .status(TripStatus.CANCELLED)
                .updatedAt(OffsetDateTime.now())
                .updatedBy(command.merchantId())
                .build();

        tripAggregateRepositoryPort.save(cancelled);
        return DeleteTripResult.builder()
                .tripId(cancelled.getId())
                .status(cancelled.getStatus())
                .build();
    }

    @Override
    public FetchTripDetailResult fetchDetail(FetchTripDetailQuery query) {
        TripAggregate trip = findTrip(query.tripId(), query.merchantId(), query.context().requestId(),
                query.context().requestDateTime(), query.context().channel());

        RouteAggregate route = routeAggregateRepositoryPort.findById(trip.getRouteId())
                .orElseThrow(() -> new BusinessException(query.context().requestId(), query.context().requestDateTime(), query.context().channel(),
                        ExceptionUtils.buildResultResponse(RECORD_NOT_FOUND, String.format(ROUTE_NOT_FOUND, trip.getRouteId()))));

        if (query.status() != null && query.status() != trip.getStatus()) {
            throw new BusinessException(query.context().requestId(), query.context().requestDateTime(), query.context().channel(),
                    ExceptionUtils.buildResultResponse(RECORD_NOT_FOUND, String.format(TRIP_NOT_FOUND, query.tripId())));
        }

        return toFetchDetailResult(trip, route);
    }

    @Override
    public FetchTripListResult fetchTripList(FetchTripListQuery query) {
        int pageSize = ApiRequestUtils.parseIntOrDefault(query.pageSize(), DEFAULT_PAGE_SIZE, "pageSize",
                query.context().requestId(), query.context().requestDateTime(), query.context().channel());
        int pageNumber = ApiRequestUtils.parseIntOrDefault(query.pageNumber(), DEFAULT_PAGE_NUMBER, "pageNumber",
                query.context().requestId(), query.context().requestDateTime(), query.context().channel());

        validatePaging(query, pageSize, pageNumber);

        PeriodWindow periodWindow = resolvePeriodWindow(query);

        PagedResult<TripAggregate> page = tripAggregateRepositoryPort.fetch(
                query.context().merchantId(),
                query.status(),
                normalizeBlank(query.rawDepartureDate()),
                periodWindow.from(),
                periodWindow.to(),
                pageNumber - 1,
                pageSize
        );

        List<String> routeIds = page.getItems().stream()
                .map(TripAggregate::getRouteId)
                .distinct()
                .filter(Objects::nonNull)
                .toList();

        List<RouteAggregate> routeAggregateList = routeAggregateRepositoryPort.findByIdIn(routeIds);

        Map<String, RouteAggregate> routeMap = routeAggregateList.stream()
                .collect(Collectors.toMap(
                        RouteAggregate::getId,
                        route -> route
                ));

        return FetchTripListResult.builder()
                .items(page.getItems().stream()
                        .map(item -> {
                            RouteAggregate route = routeMap.get(item.getRouteId());
                            return toFetchDetailResult(item, route);
                        })
                        .toList())
                .pageNumber(page.getPageNumber() + 1)
                .pageSize(page.getPageSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
    }

    @Override
    @Transactional
    public AssignRouteResult assignRoute(AssignRouteCommand command) {
        if (tripAssignmentRepositoryPort.existsActiveByTripId(command.tripId(), command.merchantId())) {
            throw new BusinessException(command.context().requestId(), command.context().requestDateTime(), command.context().channel(),
                    ExceptionUtils.buildResultResponse(DUPLICATE_ERROR, String.format(DUPLICATE_ROUTE_ASSIGNMENT, command.tripId())));
        }

        VehicleProfile vehicle = vehicleProfileRepositoryPort.findById(command.vehicleId(), command.merchantId())
                .orElseThrow(() -> new BusinessException(command.context().requestId(), command.context().requestDateTime(), command.context().channel(),
                        ExceptionUtils.buildResultResponse(RECORD_NOT_FOUND, VEHICLE_NOT_FOUND)));

        TripAggregate trip = tripAggregateRepositoryPort.findById(command.tripId(), command.merchantId())
                .orElseThrow(() -> new BusinessException(command.context().requestId(), command.context().requestDateTime(), command.context().channel(),
                        ExceptionUtils.buildResultResponse(RECORD_NOT_FOUND, String.format(TRIP_NOT_FOUND, command.tripId()))));

        RouteAggregate route = routeAggregateRepositoryPort.findById(trip.getRouteId(), command.merchantId())
                .orElseThrow(() -> new BusinessException(command.context().requestId(), command.context().requestDateTime(), command.context().channel(),
                        ExceptionUtils.buildResultResponse(RECORD_NOT_FOUND, String.format(ROUTE_NOT_FOUND, trip.getRouteId()))));


        sLog.info("Vehicle Template Id: {}", vehicle.getTemplateId());

        VehicleTemplate vehicleTemplate = vehicleTemplateRepositoryPort.findByIdIncludingInactive(vehicle.getTemplateId(), command.merchantId())
                .orElseThrow(() -> new BusinessException(command.context().requestId(), command.context().requestDateTime(), command.context().channel(),
                        ExceptionUtils.buildResultResponse(RECORD_NOT_FOUND, VEHICLE_TEMPLATE_NOT_FOUND)));

        if (!VehicleTemplateStatus.ACTIVE.equals(vehicleTemplate.getStatus())) {
            throw new BusinessException(command.context().requestId(), command.context().requestDateTime(), command.context().channel(),
                    ExceptionUtils.buildResultResponse(INVALID_INPUT_ERROR,
                            String.format("Vehicle template %s cannot be assigned while status is %s",
                                    vehicleTemplate.getId(), vehicleTemplate.getStatus())));
        }

        DriverProfile driver = driverProfileRepositoryPort.findById(command.driverId(), command.merchantId())
                .orElseThrow(() -> new BusinessException(command.context().requestId(), command.context().requestDateTime(), command.context().channel(),
                        ExceptionUtils.buildResultResponse(RECORD_NOT_FOUND, DRIVER_NOT_FOUND_MESSAGE)));

        OffsetDateTime assignedAt = OffsetDateTime.now();

        // Dynamic Pricing: Check for Surcharge on Holidays/Peak Days
        BigDecimal basePrice = vehicleTemplate.getTicketPrice();
        BigDecimal surchargeRate = holidayService.getSurchargeRate(trip.getDepartureTime().toLocalDate());
        BigDecimal finalPrice = basePrice;

        if (surchargeRate.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal surchargeAmount = basePrice.multiply(surchargeRate)
                    .divide(new BigDecimal("100"), 0, RoundingMode.HALF_UP);
            finalPrice = basePrice.add(surchargeAmount);
            sLog.info("[DYNAMIC-PRICING] Surcharge applied: {}% for date: {}. Final Price: {}", surchargeRate, trip.getDepartureTime().toLocalDate(), finalPrice);
        }

        TripAssignmentRecord tripAssignment = TripAssignmentRecord.assign(
                UUID.randomUUID().toString(),
                command.tripId(),
                command.creator(),
                trip.getMerchantId(),
                vehicle.getId(),
                command.driverId(),
                finalPrice,
                assignedAt
        );
        tripAssignmentRepositoryPort.save(tripAssignment);

        sLog.info("[ASSIGN-ROUTE] Trip assignment queued with vehicleId: {} driverId: {}", vehicle.getId(), command.driverId());

        TripSellableEvent sellableEvent = TripSellableEvent
                .builder()
                .tripId(tripAssignment.getTripId())
                .vehicleId(tripAssignment.getVehicleId())
                .driverId(tripAssignment.getDriverId())
                .assignedBy(command.creator())
                .assignedAt(tripAssignment.getAssignedAt())
                .status(trip.getStatus())
                .seatCount(vehicleTemplate.getSeatCapacity())
                .hasFloor(vehicle.isHasFloor())
                .creator(command.creator())
                .build();

        outBoxService.generateEvent(tripAssignment.getTripId(), tripTopic, tripReadyForSaleEvent, tripAssignment.getId(), sellableEvent, ApiRequestUtils.getHeader(command.context()));
        publishTripAssignedActivity(command, tripAssignment, route, trip, vehicle);

        return AssignRouteResult.builder()
                .creator(command.creator())
                .assignedAt(tripAssignment.getAssignedAt().toString())
                .tripId(tripAssignment.getTripId())
                .vehicleId(tripAssignment.getVehicleId())
                .driverId(tripAssignment.getDriverId())
                .status(tripAssignment.getStatus().name())
                .build();
    }

    private void publishTripAssignedActivity(AssignRouteCommand command,
                                             TripAssignmentRecord assignment,
                                             RouteAggregate route,
                                             TripAggregate trip,
                                             VehicleProfile vehicle) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("tripId", assignment.getTripId());
        metadata.put("routeId", trip.getRouteId());
        metadata.put("vehicleId", assignment.getVehicleId());
        metadata.put("driverId", assignment.getDriverId());
        metadata.put("originName", route.getOriginName());
        metadata.put("destinationName", route.getDestinationName());
        metadata.put("departureTime", trip.getDepartureTime());

        String routeLabel = route.getOriginName() + " - " + route.getDestinationName();
        String vehicleDisplay = vehicle.getVehiclePlate() == null || vehicle.getVehiclePlate().isBlank()
                ? assignment.getVehicleId()
                : vehicle.getVehiclePlate();

        recentActivityPublisher.publishMerchantActivity(
                command.merchantId(),
                "TRIP_ASSIGNED",
                assignment.getTripId(),
                "INFO",
                "SUCCESS",
                "platform-merchant",
                command.context() == null ? null : command.context().requestId(),
                "Trip assigned",
                "Trip " + routeLabel + " assigned to vehicle " + vehicleDisplay,
                command.creator(),
                command.creator(),
                "TRIP",
                assignment.getTripId(),
                routeLabel,
                metadata
        );
    }


    private void validatePaging(FetchTripListQuery query, int pageSize, int pageNumber) {
        if (pageSize < 1 || pageSize > 100) {
            throw new BusinessException(query.context().requestId(), query.context().requestDateTime(), query.context().channel(),
                    ExceptionUtils.buildResultResponse(INVALID_INPUT_ERROR, INVALID_PAGE_SIZE));
        }
        if (pageNumber < 1) {
            throw new BusinessException(query.context().requestId(), query.context().requestDateTime(), query.context().channel(),
                    ExceptionUtils.buildResultResponse(INVALID_INPUT_ERROR, INVALID_PAGE_NUMBER));
        }
    }
    private PeriodWindow resolvePeriodWindow(FetchTripListQuery query) {
        LocalDate rawDepartureDate = parseRawDepartureDate(query.rawDepartureDate());
        if (rawDepartureDate != null) {
            return new PeriodWindow(toStartOfDay(rawDepartureDate), toStartOfDay(rawDepartureDate.plusDays(1)));
        }

        LocalDate today = LocalDate.now(DEFAULT_ZONE);
        String period = normalizeBlank(query.period());
        period = period == null ? PERIOD_MONTH : period.toUpperCase();
        int year = parseOptionalInt(query.year(), today.getYear(), "year", query);

        return switch (period) {
            case PERIOD_MONTH -> {
                int month = parseOptionalInt(query.month(), today.getMonthValue(), "month", query);
                validateRange(month, 1, 12, "month", query);
                YearMonth yearMonth = YearMonth.of(year, month);
                yield new PeriodWindow(toStartOfDay(yearMonth.atDay(1)), toStartOfDay(yearMonth.plusMonths(1).atDay(1)));
            }
            case PERIOD_QUARTER -> {
                int quarter = parseOptionalInt(query.quarter(), ((today.getMonthValue() - 1) / 3) + 1, "quarter", query);
                validateRange(quarter, 1, 4, "quarter", query);
                int startMonth = ((quarter - 1) * 3) + 1;
                LocalDate startDate = LocalDate.of(year, startMonth, 1);
                yield new PeriodWindow(toStartOfDay(startDate), toStartOfDay(startDate.plusMonths(3)));
            }
            case PERIOD_YEAR -> {
                LocalDate startDate = LocalDate.of(year, 1, 1);
                yield new PeriodWindow(toStartOfDay(startDate), toStartOfDay(startDate.plusYears(1)));
            }
            default -> throw new BusinessException(query.context().requestId(), query.context().requestDateTime(), query.context().channel(),
                    ExceptionUtils.buildResultResponse(INVALID_INPUT_ERROR, "period must be MONTH, QUARTER or YEAR"));
        };
    }

    private LocalDate parseRawDepartureDate(String value) {
        if (!hasText(value)) {
            return null;
        }
        try {
            return LocalDate.parse(value.trim());
        } catch (RuntimeException ignored) {
            return null;
        }
    }

    private int parseOptionalInt(String value, int defaultValue, String field, FetchTripListQuery query) {
        return ApiRequestUtils.parseIntOrDefault(
                value,
                defaultValue,
                field,
                query.context().requestId(),
                query.context().requestDateTime(),
                query.context().channel()
        );
    }

    private void validateRange(int value, int min, int max, String field, FetchTripListQuery query) {
        if (value < min || value > max) {
            throw new BusinessException(query.context().requestId(), query.context().requestDateTime(), query.context().channel(),
                    ExceptionUtils.buildResultResponse(INVALID_INPUT_ERROR, field + " must be in [" + min + ".." + max + "]"));
        }
    }

    private OffsetDateTime toStartOfDay(LocalDate date) {
        return date.atTime(LocalTime.MIN).atZone(DEFAULT_ZONE).toOffsetDateTime();
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private String normalizeBlank(String value) {
        return hasText(value) ? value.trim() : null;
    }

    private record PeriodWindow(OffsetDateTime from, OffsetDateTime to) {
    }

    private RouteAggregate findRoute(String routeId, String merchantId, String requestId, String requestDateTime, String channel) {
        return routeAggregateRepositoryPort.findById(routeId, merchantId)
                .orElseThrow(() -> new BusinessException(requestId, requestDateTime, channel,
                        ExceptionUtils.buildResultResponse(RECORD_NOT_FOUND, String.format(ROUTE_NOT_FOUND, routeId))));
    }

    private TripAggregate findTrip(String tripId, String merchantId, String requestId, String requestDateTime, String channel) {
        return tripAggregateRepositoryPort.findById(tripId, merchantId)
                .orElseThrow(() -> new BusinessException(requestId, requestDateTime, channel,
                        ExceptionUtils.buildResultResponse(RECORD_NOT_FOUND, String.format(TRIP_NOT_FOUND, tripId))));
    }

    private CreateTripResult toCreateResult(TripAggregate trip) {
        return CreateTripResult.builder()
                .tripId(trip.getId())
                .routeId(trip.getRouteId())
                .merchantId(trip.getMerchantId())
                .departureTime(trip.getDepartureTime())
                .rawDepartureTime(trip.getRawDepartureTime())
                .rawDepartureDate(trip.getRawDepartureDate())
                .status(trip.getStatus())
                .build();
    }

    private UpdateTripResult toUpdateResult(TripAggregate trip) {
        return UpdateTripResult.builder()
                .tripId(trip.getId())
                .routeId(trip.getRouteId())
                .merchantId(trip.getMerchantId())
                .departureTime(trip.getDepartureTime())
                .rawDepartureTime(trip.getRawDepartureTime())
                .rawDepartureDate(trip.getRawDepartureDate())
                .status(trip.getStatus())
                .build();
    }

    private FetchTripDetailResult toFetchDetailResult(TripAggregate trip, RouteAggregate route) {
        return FetchTripDetailResult.builder()
                .tripId(trip.getId())
                .creator(trip.getCreator())
                .tripCode(trip.getTripCode())
                .departureTime(trip.getDepartureTime())
                .rawDepartureTime(trip.getRawDepartureTime())
                .rawDepartureDate(trip.getRawDepartureDate())
                .rawArrivalTime(calculateArrivalTime(trip.getRawDepartureTime(), route.getDuration()))
                .status(trip.getStatus())
                .route(FetchTripDetailResult.FetchTripDetailRoute.builder()
                        .routeId(route.getId())
                        .originName(route.getOriginName())
                        .originCode(route.getOriginCode())
                        .originDepartmentId(route.getOriginDepartmentId())
                        .destinationName(route.getDestinationName())
                        .destinationCode(route.getDestinationCode())
                        .destinationDepartmentId(route.getDestinationDepartmentId())
                        .duration(route.getDuration())
                        .build())
                .build();
    }

    private String calculateArrivalTime(String rawDepartureTime, Long durationMinutes) {
        if (rawDepartureTime == null || durationMinutes == null) return null;
        try {
            String[] parts = rawDepartureTime.split(":");
            int hours = Integer.parseInt(parts[0]);
            int minutes = Integer.parseInt(parts[1]);

            int totalMinutes = hours * 60 + minutes + durationMinutes.intValue();
            int arrivalHours = (totalMinutes / 60) % 24;
            int arrivalMinutes = totalMinutes % 60;

            return String.format("%02d:%02d", arrivalHours, arrivalMinutes);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public AssignRouteBatchResult assignRouteBatch(AssignRouteBatchCommand command) {
        sLog.info("[ASSIGN-BATCH] Starting batch assignment for {} items, merchantId: {}",
                command.assignments().size(), command.merchantId());

        List<AssignRouteResult> successItems = new ArrayList<>();
        List<AssignRouteBatchFailedItem> failedItems = new ArrayList<>();

        TransactionTemplate txTemplate = new TransactionTemplate(transactionManager);
        txTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);

        for (AssignRouteBatchCommand.AssignRouteBatchItem item : command.assignments()) {
            try {
                AssignRouteResult result = txTemplate.execute(status -> assignRoute(AssignRouteCommand.builder()
                        .merchantId(command.merchantId())
                        .creator(command.creator())
                        .tripId(item.tripId())
                        .vehicleId(item.vehicleId())
                        .driverId(item.driverId())
                        .context(command.context())
                        .build()));
                if (result != null) {
                    successItems.add(result);
                }
            } catch (Exception e) {
                sLog.warn("[ASSIGN-BATCH] Failed to assign tripId={}: {}", item.tripId(), e.getMessage());
                failedItems.add(AssignRouteBatchFailedItem.builder()
                        .tripId(item.tripId())
                        .driverId(item.driverId())
                        .vehicleId(item.vehicleId())
                        .errorCode(resolveErrorCode(e))
                        .errorMessage(e.getMessage())
                        .build());
            }
        }

        sLog.info("[ASSIGN-BATCH] Completed. Success: {}, Failed: {}", successItems.size(), failedItems.size());
        return AssignRouteBatchResult.builder()
                .successCount(successItems.size())
                .failedCount(failedItems.size())
                .successItems(successItems)
                .failedItems(failedItems)
                .build();
    }

    private String resolveErrorCode(Exception e) {
        if (e instanceof BusinessException) {
            return "BUSINESS_VALIDATION_ERROR";
        }
        return "INTERNAL_ERROR";
    }

    @Override
    @Transactional
    public ScheduleAsyncResult scheduleAsync(ScheduleAsyncCommand command) {
        String jobId = UUID.randomUUID().toString();

        sLog.info("Schedule Async Command: {}", command);
        sLog.info("[SCHEDULE-ASYNC] Creating Optimization Job: {}, Route: {}, Merchant: {}",
                jobId, command.routeId(), command.merchantId());

        OptimizationJobEntity job = OptimizationJobEntity.builder()
                .id(jobId)
                .merchantId(command.merchantId())
                .routeId(command.routeId())
                .status(OptimizationJobStatus.PROCESSING)
                .creatorEmail(command.context() != null ? command.context().userEmail() : "admin@routex.com")
                .build();

        optimizationJobRepository.save(job);

        // Prepare Kafka event payload
        AiOptimizationRequestedEvent event = AiOptimizationRequestedEvent.builder()
                .jobId(jobId)
                .merchantId(command.merchantId())
                .routeId(command.routeId())
                .demands(command.demands().stream()
                        .map(d -> AiOptimizationRequestedEvent.DemandEntry.builder()
                                .date(d.date())
                                .demand(d.demand())
                                .build())
                        .collect(Collectors.toList()))
                .operatingHours(command.operatingHours())
                .operatingCostPerTrip(command.operatingCostPerTrip())
                .maxTripsAllowed(command.maxTripsAllowed())
                .minLoadFactor(command.minLoadFactor())
                .build();

        // Enqueue to Outbox table to be processed by OutBoxService pipeline
        outBoxService.generateEvent(
                jobId,
                "routex.ai.optimization.requested",
                "AiOptimizationRequestedEvent",
                jobId,
                event,
                ApiRequestUtils.getHeader(command.context())
        );

        sLog.info("[SCHEDULE-ASYNC] Job successfully initialized: {}", jobId);

        return ScheduleAsyncResult.builder()
                .jobId(jobId)
                .status("PROCESSING")
                .build();
    }
}
