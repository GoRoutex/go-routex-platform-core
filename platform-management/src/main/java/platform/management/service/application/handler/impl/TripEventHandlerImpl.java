package platform.management.service.application.handler.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import platform.core.common.service.api.BaseRequest;
import platform.core.common.service.domain.trip.TripStatus;
import platform.core.common.service.domain.vehicle.VehicleStatus;
import platform.core.common.service.infrastructure.event.DomainEvent;
import platform.core.common.service.infrastructure.kafka.event.TripAssignedEvent;
import platform.core.common.service.persistence.exception.BusinessException;
import platform.core.common.service.persistence.utils.ExceptionUtils;
import platform.management.service.application.handler.TripEventHandler;
import platform.management.service.domain.assignment.port.TripAssignmentRepositoryPort;
import platform.management.service.domain.trip.model.TripAggregate;
import platform.management.service.domain.trip.port.TripAggregateRepositoryPort;
import platform.merchant.service.domain.assignment.TripAssignmentStatus;
import platform.merchant.service.domain.assignment.model.TripAssignmentRecord;
import platform.merchant.service.domain.driver.DriverStatus;
import platform.merchant.service.domain.driver.OperationStatus;
import platform.merchant.service.domain.driver.model.DriverProfile;
import platform.merchant.service.domain.driver.port.DriverProfileRepositoryPort;
import platform.merchant.service.domain.vehicle.model.VehicleProfile;
import platform.merchant.service.domain.vehicle.port.VehicleProfileRepositoryPort;
import vn.com.go.routex.identity.security.log.SystemLog;

import static platform.core.common.service.persistence.constant.ErrorConstant.DRIVER_NOT_FOUND_MESSAGE;
import static platform.core.common.service.persistence.constant.ErrorConstant.INVALID_INPUT_ERROR;
import static platform.core.common.service.persistence.constant.ErrorConstant.RECORD_NOT_FOUND;
import static platform.core.common.service.persistence.constant.ErrorConstant.ROUTE_ASSIGNMENT_NOT_FOUND;
import static platform.core.common.service.persistence.constant.ErrorConstant.ROUTE_NOT_FOUND;
import static platform.core.common.service.persistence.constant.ErrorConstant.VEHICLE_NOT_FOUND;


@Component
@RequiredArgsConstructor
public class TripEventHandlerImpl implements TripEventHandler {

    private final VehicleProfileRepositoryPort vehicleRepositoryPort;
    private final DriverProfileRepositoryPort driverProfileRepositoryPort;
    private final TripAssignmentRepositoryPort tripAssignmentRepositoryPort;
    private final TripAggregateRepositoryPort tripAggregateRepositoryPort;

    private final SystemLog sLog = SystemLog.getLogger(this.getClass());

    @Override
    @Transactional
    public void processAssignedEvent(DomainEvent event, BaseRequest context, TripAssignedEvent assignedEvent) {
        VehicleProfile vehicleProfile = vehicleRepositoryPort.findById(assignedEvent.vehicleId())
                .orElseThrow(() -> new BusinessException(context.getRequestId(), context.getRequestDateTime(), context.getChannel(),
                        ExceptionUtils.buildResultResponse(RECORD_NOT_FOUND, VEHICLE_NOT_FOUND)));

        DriverProfile driverProfile = driverProfileRepositoryPort.findById(assignedEvent.driverId())
                .orElseThrow(() -> new BusinessException(context.getRequestId(), context.getRequestDateTime(), context.getChannel(),
                        ExceptionUtils.buildResultResponse(RECORD_NOT_FOUND, DRIVER_NOT_FOUND_MESSAGE)));

        TripAssignmentRecord tripAssignmentRecord = tripAssignmentRepositoryPort.findByTripIdAndStatus(assignedEvent.tripId(), TripAssignmentStatus.PENDING_ASSIGNMENT)
                        .orElseThrow(() -> new BusinessException(context.getRequestId(), context.getRequestDateTime(), context.getChannel(),
                                ExceptionUtils.buildResultResponse(RECORD_NOT_FOUND, ROUTE_ASSIGNMENT_NOT_FOUND)));

        TripAggregate tripAggregate = tripAggregateRepositoryPort.findById(assignedEvent.tripId())
                .orElseThrow(() -> new BusinessException(context.getRequestId(), context.getRequestDateTime(), context.getChannel(),
                        ExceptionUtils.buildResultResponse(RECORD_NOT_FOUND, String.format(ROUTE_NOT_FOUND, assignedEvent.tripId()))));

        sLog.info("[TRIP-ASSIGNED] Processing eventId={} tripId={} vehicleId={} driverId={} vehicleStatus={} driverStatus={} driverOperationStatus={}",
                event.eventId(),
                assignedEvent.tripId(),
                assignedEvent.vehicleId(),
                assignedEvent.driverId(),
                vehicleProfile.getStatus(),
                driverProfile.getStatus(),
                driverProfile.getOperationStatus());

        if (VehicleStatus.IN_SERVICE.equals(vehicleProfile.getStatus())
                && OperationStatus.ON_TRIP.equals(driverProfile.getOperationStatus())
                && TripStatus.ASSIGNED.equals(tripAggregate.getStatus())
                && TripAssignmentStatus.ASSIGNED.equals(tripAssignmentRecord.getStatus())) {
            sLog.info("[TRIP-ASSIGNED] Skip eventId={} tripId={} because vehicle, driver, routes are already assigned",
                    event.eventId(), assignedEvent.tripId());
            return;
        }

        validateTrips(tripAggregate, tripAssignmentRecord, assignedEvent, context);
        validateVehicle(vehicleProfile, assignedEvent, context);
        validateDriver(driverProfile, assignedEvent, context);

        vehicleProfile.setStatus(VehicleStatus.IN_SERVICE);
        driverProfile.setOperationStatus(OperationStatus.ON_TRIP);
        tripAggregate.setStatus(TripStatus.ASSIGNED);
        tripAssignmentRecord.setStatus(TripAssignmentStatus.ASSIGNED);
        tripAssignmentRepositoryPort.save(tripAssignmentRecord);
        tripAggregateRepositoryPort.save(tripAggregate);
        driverProfileRepositoryPort.save(driverProfile);
        vehicleRepositoryPort.save(vehicleProfile);

        sLog.info("[TRIP-ASSIGNED] Updated eventId={} tripId={} vehicleId={} driverId={} vehicleStatus={} driverOperationStatus={}",
                event.eventId(),
                assignedEvent.tripId(),
                assignedEvent.vehicleId(),
                assignedEvent.driverId(),
                vehicleProfile.getStatus(),
                driverProfile.getOperationStatus());

    }

    private void validateTrips(TripAggregate routeAggregate, TripAssignmentRecord tripAssignmentRecord, TripAssignedEvent assignedEvent, BaseRequest context) {
        if(!TripStatus.SCHEDULED.equals(routeAggregate.getStatus())
        || !TripAssignmentStatus.PENDING_ASSIGNMENT.equals(tripAssignmentRecord.getStatus())) {
            throw new BusinessException(
                    context.getRequestId(),
                    context.getRequestDateTime(),
                    context.getChannel(),
                    ExceptionUtils.buildResultResponse(
                            INVALID_INPUT_ERROR,
                            String.format("Trip and Route Assignment with id %s is not yet SCHEDULED & PENDING_ASSIGNMENT",
                                    assignedEvent.tripId())
                    )
            );
        }
    }

    private void validateVehicle(VehicleProfile vehicleProfile, TripAssignedEvent assignedEvent, BaseRequest context) {
        if (VehicleStatus.MAINTENANCE.equals(vehicleProfile.getStatus())
                || VehicleStatus.BROKEN.equals(vehicleProfile.getStatus())
                || VehicleStatus.INACTIVE.equals(vehicleProfile.getStatus())) {
            throw new BusinessException(
                    context.getRequestId(),
                    context.getRequestDateTime(),
                    context.getChannel(),
                    ExceptionUtils.buildResultResponse(
                            INVALID_INPUT_ERROR,
                            String.format("Vehicle %s cannot be assigned while status is %s",
                                    assignedEvent.vehicleId(), vehicleProfile.getStatus())
                    )
            );
        }
    }

    private void validateDriver(DriverProfile driverProfile, TripAssignedEvent assignedEvent, BaseRequest context) {
        if (!DriverStatus.ACTIVE.equals(driverProfile.getStatus())) {
            throw new BusinessException(
                    context.getRequestId(),
                    context.getRequestDateTime(),
                    context.getChannel(),
                    ExceptionUtils.buildResultResponse(
                            INVALID_INPUT_ERROR,
                            String.format("Driver %s cannot be assigned while status is %s",
                                    assignedEvent.driverId(), driverProfile.getStatus())
                    )
            );
        }
    }
}
