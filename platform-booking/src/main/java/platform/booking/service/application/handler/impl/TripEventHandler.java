package platform.booking.service.application.handler.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;
import platform.booking.service.application.handler.TripEvent;
import platform.booking.service.domain.vehicle.port.VehicleSeatBlueprintQueryPort;
import platform.core.common.service.api.BaseRequest;
import platform.core.common.service.common.RequestContext;
import platform.core.common.service.domain.seat.SeatStatus;
import platform.core.common.service.domain.seat.model.TripSeat;
import platform.core.common.service.domain.seat.port.TripSeatRepositoryPort;
import platform.core.common.service.domain.trip.TripStatus;
import platform.core.common.service.domain.vehicle.VehicleStatus;
import platform.core.common.service.domain.vehicle.model.VehicleSeatBlueprint;
import platform.core.common.service.infrastructure.event.DomainEvent;
import platform.core.common.service.infrastructure.kafka.event.TripSeatGeneratedEvent;
import platform.core.common.service.infrastructure.kafka.event.TripSellableEvent;
import platform.core.common.service.infrastructure.redis.models.TripCacheSeat;
import platform.core.common.service.persistence.exception.BusinessException;
import platform.core.common.service.persistence.utils.ExceptionUtils;
import platform.merchant.service.domain.assignment.TripAssignmentStatus;
import platform.merchant.service.domain.assignment.model.TripAssignmentRecord;
import platform.merchant.service.domain.assignment.port.TripAssignmentRepositoryPort;
import platform.merchant.service.domain.driver.OperationStatus;
import platform.merchant.service.domain.driver.model.DriverProfile;
import platform.merchant.service.domain.driver.port.DriverProfileRepositoryPort;
import platform.merchant.service.domain.trip.model.TripAggregate;
import platform.merchant.service.domain.trip.port.TripAggregateRepositoryPort;
import platform.merchant.service.domain.vehicle.model.VehicleProfile;
import platform.merchant.service.domain.vehicle.port.VehicleProfileRepositoryPort;
import vn.com.go.routex.identity.security.log.SystemLog;

import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import static platform.core.common.service.persistence.constant.ErrorConstant.DUPLICATE_ERROR;
import static platform.core.common.service.persistence.constant.ErrorConstant.INVALID_INPUT_ERROR;
import static platform.core.common.service.persistence.constant.ErrorConstant.RECORD_NOT_FOUND;
import static platform.core.common.service.persistence.constant.ErrorConstant.ROUTE_SEAT_EXIST;

@Component
@RequiredArgsConstructor
public class TripEventHandler implements TripEvent {

    private final VehicleSeatBlueprintQueryPort vehicleSeatBlueprintQueryPort;
    private final TripSeatRepositoryPort tripSeatRepositoryPort;
    private final TripAssignmentRepositoryPort tripAssignmentRepositoryPort;
    private final TripAggregateRepositoryPort tripAggregateRepositoryPort;
    private final VehicleProfileRepositoryPort vehicleProfileRepositoryPort;
    private final DriverProfileRepositoryPort driverProfileRepositoryPort;
    private final PlatformTransactionManager transactionManager;
    private final ApplicationEventPublisher applicationEventPublisher;

    private final SystemLog sLog = SystemLog.getLogger(this.getClass());

    @Override
    @Transactional
    public void generateTripSeat(DomainEvent event, BaseRequest context, TripSellableEvent payload) {
        try {
            RequestContext requestContext = RequestContext.builder()
                    .requestId(context.getRequestId())
                    .requestDateTime(context.getRequestDateTime())
                    .channel(context.getChannel())
                    .build();
            TripAssignmentRecord assignment = findPendingAssignment(payload, context);
            TripAggregate trip = tripAggregateRepositoryPort.findById(payload.tripId())
                    .orElseThrow(() -> new BusinessException(context.getRequestId(), context.getRequestDateTime(), context.getChannel(),
                            ExceptionUtils.buildResultResponse(RECORD_NOT_FOUND, "Trip not found: " + payload.tripId())));
            VehicleProfile vehicle = vehicleProfileRepositoryPort.findById(payload.vehicleId())
                    .orElseThrow(() -> new BusinessException(context.getRequestId(), context.getRequestDateTime(), context.getChannel(),
                            ExceptionUtils.buildResultResponse(RECORD_NOT_FOUND, "Vehicle not found: " + payload.vehicleId())));
            DriverProfile driver = driverProfileRepositoryPort.findById(payload.driverId())
                    .orElseThrow(() -> new BusinessException(context.getRequestId(), context.getRequestDateTime(), context.getChannel(),
                            ExceptionUtils.buildResultResponse(RECORD_NOT_FOUND, "Driver not found: " + payload.driverId())));

            VehicleSeatBlueprint blueprint = vehicleSeatBlueprintQueryPort.fetchByVehicleId(payload.vehicleId(), requestContext);

            sLog.info("Blueprint: {}", blueprint);

            if (tripSeatRepositoryPort.existsByTripId(payload.tripId())) {
                throw new BusinessException(context.getRequestId(), context.getRequestDateTime(), context.getChannel(),
                        ExceptionUtils.buildResultResponse(DUPLICATE_ERROR, String.format(ROUTE_SEAT_EXIST, payload.tripId())));
            }

            sLog.info("[TRIP-SEAT] Generate seats tripId={} vehicleId={} seatCapacity={} hasFloor={}",
                    payload.tripId(), payload.vehicleId(), blueprint.getSeatCapacity(), blueprint.isHasFloor());

            Map<String, VehicleSeatBlueprint.SeatBlueprintItem> blueprintMap = blueprint.getSeats().stream()
                    .collect(Collectors.toMap(VehicleSeatBlueprint.SeatBlueprintItem::getId, Function.identity()));

            List<TripSeat> seats = blueprint.getSeats().stream()
                    .map(seatBlueprint -> TripSeat.builder()
                            .id(UUID.randomUUID().toString())
                            .tripId(payload.tripId())
                            .seatNo(seatBlueprint.getSeatCode())
                            .status(SeatStatus.AVAILABLE)
                            .seatTemplateId(seatBlueprint.getId())
                            .creator(payload.creator())
                            .createdAt(OffsetDateTime.now())
                            .createdBy(payload.creator())
                            .build())
                    .collect(Collectors.toList());

            List<TripSeat> savedSeats = tripSeatRepositoryPort.saveAll(seats);

            assignment.markAssigned(payload.assignedBy(), OffsetDateTime.now(), "ASSIGNMENT_READY", "Trip seats generated successfully");
            trip.setStatus(TripStatus.ASSIGNED);
            vehicle.setStatus(VehicleStatus.IN_SERVICE);
            driver.setOperationStatus(OperationStatus.ON_TRIP);
            tripAssignmentRepositoryPort.save(assignment);
            tripAggregateRepositoryPort.save(trip);
            vehicleProfileRepositoryPort.save(vehicle);
            driverProfileRepositoryPort.save(driver);

            List<TripCacheSeat> cacheData = savedSeats.stream()
                    .map(seat -> {
                        VehicleSeatBlueprint.SeatBlueprintItem seatBlueprint = blueprintMap.get(seat.getSeatTemplateId());
                        return TripCacheSeat.builder()
                                .tripId(seat.getTripId())
                                .seatId(seat.getId())
                                .seatNo(seat.getSeatNo())
                                .status(seat.getStatus())
                                .floor(seatBlueprint.getFloor())
                                .rowNo(seatBlueprint.getRowNo())
                                .colNo(seatBlueprint.getColumnNo())
                                .build();
                    })
                    .sorted(Comparator.comparing(TripCacheSeat::getSeatNo))
                    .toList();

            sLog.info("[TRIP-CACHE] Trip Seat Cache Data: {}", cacheData);
            applicationEventPublisher.publishEvent(new TripSeatGeneratedEvent(payload.tripId(), cacheData));
        } catch (Exception ex) {
            recordAssignmentFailure(payload, ex);
            throw ex;
        }
    }

    private TripAssignmentRecord findPendingAssignment(TripSellableEvent payload, BaseRequest context) {
        TripAssignmentRecord assignment = tripAssignmentRepositoryPort.findByTripId(payload.tripId())
                .orElseThrow(() -> new BusinessException(context.getRequestId(), context.getRequestDateTime(), context.getChannel(),
                        ExceptionUtils.buildResultResponse(RECORD_NOT_FOUND, "Trip assignment not found: " + payload.tripId())));

        if (!TripAssignmentStatus.PENDING_ASSIGNMENT.equals(assignment.getStatus())) {
            throw new BusinessException(context.getRequestId(), context.getRequestDateTime(), context.getChannel(),
                    ExceptionUtils.buildResultResponse(INVALID_INPUT_ERROR,
                            String.format("Trip assignment %s must be PENDING_ASSIGNMENT but was %s",
                                    payload.tripId(), assignment.getStatus())));
        }
        return assignment;
    }

    private void recordAssignmentFailure(TripSellableEvent payload, Exception ex) {
        TransactionTemplate template = new TransactionTemplate(transactionManager);
        template.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        template.executeWithoutResult(status -> {
            tripAssignmentRepositoryPort.findByTripId(payload.tripId()).ifPresent(assignment -> {
                if (!TripAssignmentStatus.PENDING_ASSIGNMENT.equals(assignment.getStatus())) {
                    return;
                }
                assignment.markFailed(payload.assignedBy(), OffsetDateTime.now(), resolveFailCode(ex), resolveFailDescription(ex));
                tripAssignmentRepositoryPort.save(assignment);
            });
        });
    }

    private String resolveFailCode(Exception ex) {
        if (ex instanceof BusinessException businessException && businessException.getResult() != null) {
            return businessException.getResult().getResponseCode();
        }
        return ex.getClass().getSimpleName();
    }

    private String resolveFailDescription(Exception ex) {
        if (ex instanceof BusinessException businessException && businessException.getResult() != null) {
            return businessException.getResult().getDescription();
        }
        return ex.getMessage() == null ? "Trip assignment failed" : ex.getMessage();
    }
}
