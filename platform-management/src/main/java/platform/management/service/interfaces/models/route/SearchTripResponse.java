package platform.management.service.interfaces.models.route;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import platform.core.common.service.api.BaseResponse;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class SearchTripResponse extends BaseResponse<List<SearchTripResponse.SearchTripResponseData>> {
    @Getter
    @Setter
    @NoArgsConstructor
    @SuperBuilder
    public static class SearchTripResponseData {
        private String id;
        private String merchantId;
        private String driverId;
        private String vehicleId;
        private String merchantName;
        private String originCode;
        private String originName;
        private String destinationCode;
        private String destinationName;
        private String originProvinceId;
        private String destinationProvinceId;
        private String originDepartmentId;
        private String originDepartmentName;
        private String destinationDepartmentId;
        private String destinationDepartmentName;
        private Long availableSeats;
        private OffsetDateTime departureTime;
        private String rawDepartureDate;
        private String rawDepartureTime;
        private String rawArrivalTime;
        private String vehiclePlate;
        private boolean hasFloor;
        private String tripCode;
        private BigDecimal ticketPrice;
        private List<SearchRoutePoints> routePoints;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    public static class SearchRoutePoints {
        private String id;
        private int stopOrder;
        private String routeId;
        private String note;
        private String departmentId;
        private String stopName;
        private String stopAddress;
        private String stopCity;
        private Double stopLatitude;
        private Double stopLongitude;
        private Integer timeAtDepartment;
    }
}
