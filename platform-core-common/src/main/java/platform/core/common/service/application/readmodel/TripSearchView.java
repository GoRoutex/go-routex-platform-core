package platform.core.common.service.application.readmodel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import platform.core.common.service.domain.trip.TripStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class TripSearchView {

    private TripInformation tripInformation;
    private TripAssignment assignment;
    private RouteInformation routeInformation;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    public static class TripInformation {
        private String id;
        private String creator;
        private String merchantId;
        private String tripCode;
        private OffsetDateTime departureTime;
        private String rawDepartureTime;
        private String rawDepartureDate;
        private TripStatus status;

    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    public static class TripAssignment {
        private BigDecimal ticketPrice;
        private String vehicleId;
        private String driverId;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    public static class RouteInformation {
        private String routeId;
        private String originCode;
        private String originName;
        private String destinationCode;
        private String destinationName;
        private String originProvinceId;
        private String destinationProvinceId;
        private String originDepartmentId;
        private String destinationDepartmentId;
        private Long durationMinutes;
    }

}
