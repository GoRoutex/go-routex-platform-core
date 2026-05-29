package platform.merchant.service.application.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import platform.core.common.service.common.RequestContext;
import platform.merchant.service.application.service.InternalBookingContextService;
import platform.merchant.service.domain.assignment.model.TripAssignmentRecord;
import platform.merchant.service.domain.assignment.port.TripAssignmentRepositoryPort;
import platform.merchant.service.domain.route.model.RouteAggregate;
import platform.merchant.service.domain.route.port.RouteAggregateRepositoryPort;
import platform.core.common.service.domain.seat.model.SeatTemplate;
import platform.core.common.service.domain.seat.port.SeatTemplateRepositoryPort;
import platform.merchant.service.domain.trip.model.TripAggregate;
import platform.merchant.service.domain.trip.port.TripAggregateRepositoryPort;
import platform.core.common.service.domain.vehicle.model.VehicleProfile;
import platform.core.common.service.domain.vehicle.model.VehicleTemplate;
import platform.core.common.service.domain.vehicle.port.VehicleProfileRepositoryPort;
import platform.core.common.service.domain.vehicle.port.VehicleTemplateRepositoryPort;
import platform.core.common.service.persistence.exception.BusinessException;
import platform.core.common.service.persistence.utils.ExceptionUtils;
import platform.merchant.service.interfaces.model.internal.booking.InternalBookingContextResponses;

import java.util.List;

import static platform.core.common.service.persistence.constant.ErrorConstant.RECORD_NOT_FOUND;
import static platform.core.common.service.persistence.constant.ErrorConstant.ROUTE_NOT_FOUND;
import static platform.core.common.service.persistence.constant.ErrorConstant.TRIP_ASSIGNMENT_NOT_FOUND;
import static platform.core.common.service.persistence.constant.ErrorConstant.TRIP_NOT_FOUND;
import static platform.core.common.service.persistence.constant.ErrorConstant.VEHICLE_NOT_FOUND_BY_ID;

@Service
@RequiredArgsConstructor
public class InternalBookingContextServiceImpl implements InternalBookingContextService {

    private final TripAggregateRepositoryPort tripAggregateRepositoryPort;
    private final TripAssignmentRepositoryPort tripAssignmentRepositoryPort;
    private final RouteAggregateRepositoryPort routeAggregateRepositoryPort;
    private final VehicleProfileRepositoryPort vehicleProfileRepositoryPort;
    private final VehicleTemplateRepositoryPort vehicleTemplateRepositoryPort;
    private final SeatTemplateRepositoryPort seatTemplateRepositoryPort;

    @Override
    public InternalBookingContextResponses.TripBookingContextData fetchTripBookingContext(String tripId, RequestContext context) {
        TripAggregate trip = tripAggregateRepositoryPort.findById(tripId)
                .orElseThrow(() -> notFound(context, String.format(TRIP_NOT_FOUND, tripId)));

        TripAssignmentRecord assignment = tripAssignmentRepositoryPort.findActiveByTripId(tripId)
                .orElseThrow(() -> notFound(context, TRIP_ASSIGNMENT_NOT_FOUND));

        RouteAggregate route = routeAggregateRepositoryPort.findById(trip.getRouteId())
                .orElseThrow(() -> notFound(context, String.format(ROUTE_NOT_FOUND, trip.getRouteId())));

        return InternalBookingContextResponses.TripBookingContextData.builder()
                .tripId(trip.getId())
                .routeId(route.getId())
                .merchantId(trip.getMerchantId() == null || trip.getMerchantId().isBlank() ? route.getMerchantId() : trip.getMerchantId())
                .vehicleId(assignment.getVehicleId())
                .ticketPrice(assignment.getTicketPrice())
                .originName(route.getOriginName())
                .destinationName(route.getDestinationName())
                .routeStatus(route.getStatus() == null ? null : route.getStatus().name())
                .tripStatus(trip.getStatus() == null ? null : trip.getStatus().name())
                .build();
    }

    @Override
    public InternalBookingContextResponses.VehicleSeatBlueprintData fetchVehicleSeatBlueprint(String vehicleId, RequestContext context) {
        VehicleProfile vehicle = vehicleProfileRepositoryPort.findById(vehicleId)
                .orElseThrow(() -> notFound(context, String.format(VEHICLE_NOT_FOUND_BY_ID, vehicleId)));

        VehicleTemplate template = vehicleTemplateRepositoryPort.findById(vehicle.getTemplateId())
                .orElseThrow(() -> notFound(context, String.format("Vehicle template with Id %s not found", vehicle.getTemplateId())));

        List<SeatTemplate> seatTemplates = seatTemplateRepositoryPort.findByVehicleTemplateId(template.getId());

        if (seatTemplates.isEmpty()) {
            throw notFound(context, String.format("Seat template for vehicle template %s not found", template.getId()));
        }

        return InternalBookingContextResponses.VehicleSeatBlueprintData.builder()
                .vehicleId(vehicle.getId())
                .merchantId(vehicle.getMerchantId())
                .templateId(template.getId())
                .seatCapacity(template.getSeatCapacity())
                .hasFloor(template.isHasFloor())
                .vehicleStatus(vehicle.getStatus())
                .seats(seatTemplates.stream()
                        .map(seat -> InternalBookingContextResponses.SeatBlueprintItem.builder()
                                .id(seat.getId())
                                .seatCode(seat.getSeatCode())
                                .floor(seat.getFloor())
                                .rowNo(seat.getRowNo())
                                .columnNo(seat.getColumnNo())
                                .build())
                        .toList())
                .build();
    }

    private BusinessException notFound(RequestContext context, String message) {
        return new BusinessException(
                context.requestId(),
                context.requestDateTime(),
                context.channel(),
                ExceptionUtils.buildResultResponse(RECORD_NOT_FOUND, message)
        );
    }
}
