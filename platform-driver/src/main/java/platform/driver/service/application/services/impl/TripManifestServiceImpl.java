package platform.driver.service.application.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import platform.core.common.service.persistence.exception.BusinessException;
import platform.driver.service.application.dto.manifest.GetTripManifestDriverView;
import platform.driver.service.application.dto.manifest.GetTripManifestQuery;
import platform.driver.service.application.dto.manifest.GetTripManifestSummaryView;
import platform.driver.service.application.dto.manifest.GetTripManifestVehicleView;
import platform.driver.service.application.dto.manifest.TripManifestView;
import platform.driver.service.application.services.TripManifestService;
import platform.core.common.service.domain.booking.BookingSeatStatus;
import platform.core.common.service.domain.booking.model.Booking;
import platform.core.common.service.domain.booking.model.BookingSeat;
import platform.core.common.service.domain.booking.port.BookingRepositoryPort;
import platform.core.common.service.domain.booking.port.BookingSeatRepositoryPort;
import platform.core.common.service.domain.vehicle.model.VehicleProfile;
import platform.core.common.service.domain.vehicle.port.VehicleProfileRepositoryPort;
import platform.core.common.service.persistence.utils.ExceptionUtils;
import platform.merchant.service.domain.assignment.model.TripAssignmentRecord;
import platform.merchant.service.domain.assignment.port.TripAssignmentRepositoryPort;
import platform.merchant.service.domain.driver.model.DriverProfile;
import platform.merchant.service.domain.driver.port.DriverProfileRepositoryPort;

import java.util.List;

import static platform.core.common.service.persistence.constant.ErrorConstant.BOOKING_NOT_FOUND_MESSAGE;
import static platform.core.common.service.persistence.constant.ErrorConstant.DRIVER_NOT_FOUND_MESSAGE;
import static platform.core.common.service.persistence.constant.ErrorConstant.RECORD_NOT_FOUND;
import static platform.core.common.service.persistence.constant.ErrorConstant.ROUTE_ASSIGNMENT_NOT_FOUND;
import static platform.core.common.service.persistence.constant.ErrorConstant.VEHICLE_NOT_FOUND_MESSAGE;


@Service
@RequiredArgsConstructor
public class TripManifestServiceImpl implements TripManifestService {
    private final BookingSeatRepositoryPort bookingSeatRepositoryPort;
    private final BookingRepositoryPort bookingRepositoryPort;
    private final DriverProfileRepositoryPort driverProfileRepositoryPort;
    private final VehicleProfileRepositoryPort vehicleRepositoryPort;
    private final TripAssignmentRepositoryPort tripAssignmentRepositoryPort;

    /**
     * Fetching Trip Manifest for Driver/Admins
     */

    @Override
    public TripManifestView getTripManifest(GetTripManifestQuery query) {

        TripAssignmentRecord tripAssignment = tripAssignmentRepositoryPort.findByTripId(query.routeId())
                .orElseThrow(() -> new BusinessException(query.context().requestId(), query.context().requestDateTime(), query.context().channel(),
                        ExceptionUtils.buildResultResponse(RECORD_NOT_FOUND, ROUTE_ASSIGNMENT_NOT_FOUND)));

        Booking booking = bookingRepositoryPort.findByRouteId(query.routeId())
                .orElseThrow(() -> new BusinessException(query.context().requestId(), query.context().requestDateTime(), query.context().channel(),
                        ExceptionUtils.buildResultResponse(RECORD_NOT_FOUND, BOOKING_NOT_FOUND_MESSAGE)));

        List<BookingSeat> bookingSeat = bookingSeatRepositoryPort.findByBookingIdAndStatus(booking.getId(), BookingSeatStatus.RESERVED);

        DriverProfile driverProfile = driverProfileRepositoryPort.findById(tripAssignment.getDriverId())
                .orElseThrow(() -> new BusinessException(query.context().requestId(), query.context().requestDateTime(), query.context().channel(),
                        ExceptionUtils.buildResultResponse(RECORD_NOT_FOUND, DRIVER_NOT_FOUND_MESSAGE)));

        VehicleProfile vehicle = vehicleRepositoryPort.findById(tripAssignment.getVehicleId())
                .orElseThrow(() -> new BusinessException(query.context().requestId(), query.context().requestDateTime(), query.context().channel(),
                        ExceptionUtils.buildResultResponse(RECORD_NOT_FOUND, VEHICLE_NOT_FOUND_MESSAGE)));



        int bookedSeats = bookingSeat.size();

        return TripManifestView.builder()
                .driverInfo(GetTripManifestDriverView.builder()
                        .driverId(driverProfile.getId())
                        .phoneNumber(driverProfile.getPhoneNumber())
                        .fullName(driverProfile.getFullName())
                        .build())
                .vehicleInfo(GetTripManifestVehicleView.builder()
                        .vehicleId(vehicle.getId())
//                        .vehicleType(vehicle.getType().name())
                        .plate(vehicle.getVehiclePlate())
//                        .totalSeats(vehicle.getSeatCapacity())
                        .build())
                .summary(GetTripManifestSummaryView.builder()
//                        .totalSeats(vehicle.getSeatCapacity())
                        .bookedSeats(bookingSeat.size())
                        .checkedInSeats(1)
                        .boardedSeats(1)
                        .cancelledSeats(1)
                        .availableSeats(1)
                        .build())
                .build();
    }
}
