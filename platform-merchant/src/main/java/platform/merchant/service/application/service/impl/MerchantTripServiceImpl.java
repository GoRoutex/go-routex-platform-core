package platform.merchant.service.application.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;
import platform.core.common.service.persistence.exception.BusinessException;
import platform.core.common.service.persistence.utils.ApiRequestUtils;
import platform.core.common.service.persistence.utils.ExceptionUtils;
import platform.core.common.service.application.service.OutBoxService;
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
import platform.core.common.service.application.command.common.PagedResult;
import platform.merchant.service.domain.job.OptimizationJobStatus;
import platform.merchant.service.domain.route.model.RouteAggregate;
import platform.merchant.service.domain.route.port.RouteAggregateRepositoryPort;
import platform.core.common.service.domain.trip.TripStatus;
import platform.merchant.service.domain.trip.model.TripAggregate;
import platform.merchant.service.domain.trip.port.TripAggregateRepositoryPort;
import platform.core.common.service.domain.vehicle.model.VehicleProfile;
import platform.core.common.service.domain.vehicle.model.VehicleTemplate;
import platform.core.common.service.domain.vehicle.port.VehicleProfileRepositoryPort;
import platform.core.common.service.domain.vehicle.port.VehicleTemplateRepositoryPort;
import platform.core.common.service.infrastructure.kafka.event.AiOptimizationRequestedEvent;
import platform.core.common.service.infrastructure.kafka.event.TripAssignedEvent;
import platform.core.common.service.infrastructure.kafka.event.TripSellableEvent;
import platform.merchant.service.infrastructure.persistence.jpa.job.entity.OptimizationJobEntity;
import platform.merchant.service.infrastructure.persistence.jpa.job.repository.OptimizationJobRepository;
import vn.com.go.routex.identity.security.log.SystemLog;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

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
import static platform.core.common.service.persistence.constant.ApplicationConstant.DEFAULT_PAGE_NUMBER;
import static platform.core.common.service.persistence.constant.ApplicationConstant.DEFAULT_PAGE_SIZE;


@Service
@RequiredArgsConstructor
public class MerchantTripServiceImpl implements MerchantTripService {

    private final TripAggregateRepositoryPort tripAggregateRepositoryPort;
    private final RouteAggregateRepositoryPort routeAggregateRepositoryPort;
    private final TripAssignmentRepositoryPort tripAssignmentRepositoryPort;
    private final VehicleProfileRepositoryPort vehicleProfileRepositoryPort;
    private final VehicleTemplateRepositoryPort vehicleTemplateRepositoryPort;
    private final OutBoxService outBoxService;
    private final HolidayService holidayService;
    private final PlatformTransactionManager transactionManager;
    private final OptimizationJobRepository optimizationJobRepository;

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
                .createdBy(command.merchantId())
                .updatedAt(now)
                .updatedBy(command.merchantId())
                .build();

        sLog.info("Trip: {}", trip);
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

        PagedResult<TripAggregate> page=  tripAggregateRepositoryPort.fetch(query.context().merchantId(), query.status(), query.rawDepartureDate(), pageNumber - 1, pageSize);

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

        VehicleTemplate vehicleTemplate = vehicleTemplateRepositoryPort.findById(vehicle.getTemplateId(), command.merchantId())
                .orElseThrow(() -> new BusinessException(command.context().requestId(), command.context().requestDateTime(), command.context().channel(),
                        ExceptionUtils.buildResultResponse(RECORD_NOT_FOUND, VEHICLE_TEMPLATE_NOT_FOUND)));

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

        TripAssignmentRecord routeAssignment = TripAssignmentRecord.assign(
                UUID.randomUUID().toString(),
                command.tripId(),
                command.creator(),
                trip.getMerchantId(),
                vehicle.getId(),
                command.driverId(),
                finalPrice,
                assignedAt
        );
        tripAssignmentRepositoryPort.save(routeAssignment);

        sLog.info("[ASSIGN-ROUTE] Trip Assigned successfully with vehicleId: {} driverId: {}", vehicle.getId(), command.driverId());

        TripSellableEvent sellableEvent = TripSellableEvent
                .builder()
                .tripId(routeAssignment.getTripId())
                .vehicleId(routeAssignment.getVehicleId())
                .assignedBy(command.creator())
                .assignedAt(routeAssignment.getAssignedAt())
                .status(TripStatus.ASSIGNED)
                .seatCount(vehicleTemplate.getSeatCapacity())
                .hasFloor(vehicle.isHasFloor())
                .creator(command.creator())
                .build();

        outBoxService.generateEvent(routeAssignment.getTripId(), tripTopic, tripReadyForSaleEvent, routeAssignment.getId(), sellableEvent, ApiRequestUtils.getHeader(command.context()));

        TripAssignedEvent assignedEvent = TripAssignedEvent
                .builder()
                .tripId(routeAssignment.getTripId())
                .driverId(routeAssignment.getDriverId())
                .vehicleId(routeAssignment.getVehicleId())
                .originName(route.getOriginName())
                .destinationName(route.getDestinationName())
                .departureTime(trip.getDepartureTime())
                .status(trip.getStatus())
                .assignedBy(command.creator())
                .assignedAt(routeAssignment.getAssignedAt())
                .build();

        outBoxService.generateEvent(routeAssignment.getTripId(), tripTopic, tripAssignedEvent, routeAssignment.getId(), assignedEvent, ApiRequestUtils.getHeader(command.context()));

        return AssignRouteResult.builder()
                .creator(command.creator())
                .assignedAt(routeAssignment.getAssignedAt().toString())
                .tripId(routeAssignment.getTripId())
                .vehicleId(routeAssignment.getVehicleId())
                .driverId(routeAssignment.getDriverId())
                .status(routeAssignment.getStatus().name())
                .build();
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
