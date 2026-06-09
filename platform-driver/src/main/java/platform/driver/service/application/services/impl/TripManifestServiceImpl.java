package platform.driver.service.application.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import platform.core.common.service.domain.ticket.TicketStatus;
import platform.core.common.service.domain.ticket.model.Ticket;
import platform.core.common.service.domain.ticket.port.TicketRepositoryPort;
import platform.core.common.service.persistence.exception.BusinessException;
import platform.core.common.service.persistence.utils.ExceptionUtils;
import platform.driver.service.application.dto.manifest.GetTripManifestBookingView;
import platform.driver.service.application.dto.manifest.GetTripManifestDriverView;
import platform.driver.service.application.dto.manifest.GetTripManifestQuery;
import platform.driver.service.application.dto.manifest.GetTripManifestSummaryView;
import platform.driver.service.application.dto.manifest.GetTripManifestVehicleView;
import platform.driver.service.application.dto.manifest.TripManifestView;
import platform.driver.service.application.services.TripManifestService;
import platform.merchant.service.domain.assignment.model.TripAssignmentRecord;
import platform.merchant.service.domain.assignment.port.TripAssignmentRepositoryPort;
import platform.merchant.service.domain.driver.model.DriverProfile;
import platform.merchant.service.domain.driver.port.DriverProfileRepositoryPort;
import platform.merchant.service.domain.trip.port.TripAggregateRepositoryPort;
import platform.merchant.service.domain.vehicle.model.VehicleProfile;
import platform.merchant.service.domain.vehicle.model.VehicleTemplate;
import platform.merchant.service.domain.vehicle.port.VehicleProfileRepositoryPort;
import platform.merchant.service.domain.vehicle.port.VehicleTemplateRepositoryPort;

import java.util.List;

import static platform.core.common.service.persistence.constant.ErrorConstant.DRIVER_NOT_FOUND_MESSAGE;
import static platform.core.common.service.persistence.constant.ErrorConstant.RECORD_NOT_FOUND;
import static platform.core.common.service.persistence.constant.ErrorConstant.ROUTE_ASSIGNMENT_NOT_FOUND;
import static platform.core.common.service.persistence.constant.ErrorConstant.TRIP_NOT_FOUND;
import static platform.core.common.service.persistence.constant.ErrorConstant.VEHICLE_NOT_FOUND_MESSAGE;


@Service
@RequiredArgsConstructor
public class TripManifestServiceImpl implements TripManifestService {
    private final TicketRepositoryPort ticketRepositoryPort;
    private final DriverProfileRepositoryPort driverProfileRepositoryPort;
    private final VehicleProfileRepositoryPort vehicleRepositoryPort;
    private final VehicleTemplateRepositoryPort vehicleTemplateRepositoryPort;
    private final TripAssignmentRepositoryPort tripAssignmentRepositoryPort;
    private final TripAggregateRepositoryPort tripAggregateRepositoryPort;

    /**
     * Fetching Trip Manifest for Driver/Admins
     */

    @Override
    public TripManifestView getTripManifest(GetTripManifestQuery query) {

        TripAssignmentRecord tripAssignment = tripAssignmentRepositoryPort.findByTripId(query.routeId())
                .orElseThrow(() -> new BusinessException(query.context().requestId(), query.context().requestDateTime(), query.context().channel(),
                        ExceptionUtils.buildResultResponse(RECORD_NOT_FOUND, ROUTE_ASSIGNMENT_NOT_FOUND)));

        tripAggregateRepositoryPort.findById(query.routeId())
                .orElseThrow(() -> new BusinessException(query.context().requestId(), query.context().requestDateTime(), query.context().channel(),
                        ExceptionUtils.buildResultResponse(RECORD_NOT_FOUND, String.format(TRIP_NOT_FOUND, query.routeId()))));

        List<Ticket> tickets = ticketRepositoryPort.findAllByTripId(query.routeId());

        DriverProfile driverProfile = driverProfileRepositoryPort.findById(tripAssignment.getDriverId())
                .orElseThrow(() -> new BusinessException(query.context().requestId(), query.context().requestDateTime(), query.context().channel(),
                        ExceptionUtils.buildResultResponse(RECORD_NOT_FOUND, DRIVER_NOT_FOUND_MESSAGE)));

        VehicleProfile vehicle = vehicleRepositoryPort.findById(tripAssignment.getVehicleId())
                .orElseThrow(() -> new BusinessException(query.context().requestId(), query.context().requestDateTime(), query.context().channel(),
                        ExceptionUtils.buildResultResponse(RECORD_NOT_FOUND, VEHICLE_NOT_FOUND_MESSAGE)));

        VehicleTemplate template = vehicle.getTemplateId() == null ? null :
                vehicleTemplateRepositoryPort.findById(vehicle.getTemplateId()).orElse(null);

        return TripManifestView.builder()
                .requestContext(query.context())
                .bookingInfo(tickets.stream()
                        .map(this::toBookingView)
                        .toList())
                .driverInfo(GetTripManifestDriverView.builder()
                        .driverId(driverProfile.getId())
                        .phoneNumber(driverProfile.getPhoneNumber())
                        .fullName(driverProfile.getFullName())
                        .build())
                .vehicleInfo(GetTripManifestVehicleView.builder()
                        .vehicleId(vehicle.getId())
                        .plate(vehicle.getVehiclePlate())
                        .vehicleType(template != null ? template.getName() : null)
                        .totalSeats(template != null && template.getSeatCapacity() != null ? template.getSeatCapacity().intValue() : null)
                        .build())
                .summary(toSummaryView(tickets, template))
                .build();
    }

    private GetTripManifestSummaryView toSummaryView(List<Ticket> tickets, VehicleTemplate template) {
        Integer totalSeats = template != null && template.getSeatCapacity() != null ? template.getSeatCapacity().intValue() : null;
        int bookedSeats = (int) tickets.stream()
                .filter(t -> t.getStatus() != TicketStatus.CANCELLED && t.getStatus() != TicketStatus.EXPIRED)
                .count();
        return GetTripManifestSummaryView.builder()
                .totalSeats(totalSeats)
                .bookedSeats(bookedSeats)
                .checkedInSeats((int) tickets.stream().filter(t -> t.getStatus() == TicketStatus.CHECKED_IN).count())
                .boardedSeats((int) tickets.stream().filter(t -> t.getStatus() == TicketStatus.BOARDED).count())
                .cancelledSeats((int) tickets.stream().filter(t -> t.getStatus() == TicketStatus.CANCELLED).count())
                .availableSeats(totalSeats != null ? Math.max(totalSeats - bookedSeats, 0) : null)
                .build();
    }

    private GetTripManifestBookingView toBookingView(Ticket ticket) {
        return GetTripManifestBookingView.builder()
                .bookingId(ticket.getBookingId())
                .bookingCode(null)
                .ticketId(ticket.getId())
                .passengerName(ticket.getCustomerName())
                .passengerPhoneNumber(ticket.getCustomerPhone())
                .seatNumber(ticket.getSeatNumber())
                .pickupPointId(ticket.getPickupStopId())
                .pickupPointName(ticket.getPickupAddress())
                .dropOffPointId(ticket.getDropOffStopId())
                .dropOffPointName(ticket.getDropOffAddress())
                .status(ticket.getStatus())
                .checkedInAt(ticket.getCheckedInAt())
                .checkedInBy(ticket.getCheckedInBy())
                .boardedAt(ticket.getBoardedAt())
                .boardedBy(ticket.getBoardedBy())
                .build();
    }
}
