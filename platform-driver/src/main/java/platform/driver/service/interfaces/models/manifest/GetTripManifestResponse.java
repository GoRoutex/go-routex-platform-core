package platform.driver.service.interfaces.models.manifest;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import platform.core.common.service.api.BaseResponse;
import platform.merchant.service.domain.route.RouteStatus;

import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class GetTripManifestResponse extends BaseResponse<GetTripManifestResponse.GetTripManifestResponseData> {

    private List<GetTripManifestBookingData> bookingInfo;
    private GetTripManifestDriverData driverInfo;
    private GetTripManifestVehicleData vehicleInfo;
    private GetTripManifestSummary summary;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    public static class GetTripManifestSummary {
        private Integer totalSeats;
        private Integer bookedSeats;
        private Integer checkedInSeats;
        private Integer boardedSeats;
        private Integer cancelledSeats;
        private Integer availableSeats;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @SuperBuilder
    public static class GetTripManifestVehicleData {
        private String vehicleId;
        private String plate;
        private String vehicleType;
        private Integer totalSeats;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @SuperBuilder
    public static class GetTripManifestDriverData {
        private String driverId;
        private String fullName;
        private String phoneNumber;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @SuperBuilder
    public static class GetTripManifestBookingData {
        private String bookingId;
        private String bookingCode;
        private String ticketId;
        private String passengerName;
        private String passengerPhoneNumber;
        private String seatNumber;
        private String pickupPointId;
        private String pickupPointName;
        private String dropOffPointId;
        private String dropOffPointName;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @SuperBuilder
    public static class GetTripManifestResponseData {
        private String routeId;
        private String routeCode;
        private String origin;
        private String destination;
        private OffsetDateTime plannedStartTime;
        private OffsetDateTime plannedEndTime;
        private RouteStatus status;
    }
}
