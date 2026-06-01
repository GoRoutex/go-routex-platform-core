package platform.driver.service.interfaces.mapper;


import org.springframework.stereotype.Component;
import platform.core.common.service.api.ApiResult;
import platform.driver.service.application.dto.manifest.GetTripManifestBookingView;
import platform.driver.service.application.dto.manifest.GetTripManifestDriverView;
import platform.driver.service.application.dto.manifest.GetTripManifestSummaryView;
import platform.driver.service.application.dto.manifest.GetTripManifestVehicleView;
import platform.driver.service.application.dto.manifest.TripManifestView;
import platform.driver.service.interfaces.models.manifest.GetTripManifestResponse;
import platform.driver.service.interfaces.models.manifest.GetTripManifestResponse.GetTripManifestSummary;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class TripManifestMapper {


    public GetTripManifestResponse toGetTripManifestResponse(TripManifestView view) {
        return GetTripManifestResponse.builder()
                .requestId(view.requestContext().requestId())
                .requestDateTime(view.requestContext().requestDateTime())
                .channel(view.requestContext().channel())
                .result(ApiResult.buildSuccess())
                .bookingInfo(toBookingData(view.bookingInfo()))
                .driverInfo(toDriverData(view.driverInfo()))
                .vehicleInfo(toVehicleData(view.vehicleInfo()))
                .summary(toSummaryData(view.summary()))
                .build();

    }

    private List<GetTripManifestResponse.GetTripManifestBookingData> toBookingData(List<GetTripManifestBookingView> listView) {
        return listView.stream().map(view -> GetTripManifestResponse.GetTripManifestBookingData.builder()
                        .bookingId(view.bookingId())
                        .bookingCode(view.bookingCode())
                        .ticketId(view.ticketId())
                        .passengerName(view.passengerName())
                        .passengerPhoneNumber(view.passengerPhoneNumber())
                        .seatNumber(view.seatNumber())
                        .pickupPointId(view.pickupPointId())
                        .pickupPointName(view.pickupPointName())
                        .dropOffPointId(view.dropOffPointId())
                        .dropOffPointName(view.dropOffPointName())
                        .status(view.status())
                        .checkedInAt(view.checkedInAt())
                        .checkedInBy(view.checkedInBy())
                        .boardedAt(view.boardedAt())
                        .boardedBy(view.boardedBy())
                        .build())
                .collect(Collectors.toList());
    }

    public GetTripManifestResponse.GetTripManifestDriverData toDriverData(GetTripManifestDriverView view) {
        return GetTripManifestResponse.GetTripManifestDriverData.builder()
                .driverId(view.driverId())
                .fullName(view.fullName())
                .phoneNumber(view.phoneNumber())
                .build();
    }


    public GetTripManifestResponse.GetTripManifestVehicleData toVehicleData(GetTripManifestVehicleView view) {
        return GetTripManifestResponse.GetTripManifestVehicleData.builder()
                .vehicleId(view.vehicleId())
                .vehicleType(view.vehicleType())
                .plate(view.plate())
                .totalSeats(view.totalSeats())
                .build();
    }


    public GetTripManifestSummary toSummaryData(GetTripManifestSummaryView v) {
        return GetTripManifestSummary.builder()
                .totalSeats(v.totalSeats())
                .bookedSeats(v.bookedSeats())
                .checkedInSeats(v.checkedInSeats())
                .boardedSeats(v.boardedSeats())
                .cancelledSeats(v.cancelledSeats())
                .availableSeats(v.availableSeats())
                .build();

    }
}
