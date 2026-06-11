package platform.management.service.interfaces.models.trip;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import platform.core.common.service.api.BaseResponse;
import platform.core.common.service.domain.trip.TripStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class FetchTripResponse extends BaseResponse<FetchTripResponse.FetchTripResponsePage> {

    @Getter
    @Setter
    @NoArgsConstructor
    @SuperBuilder
    public static class FetchTripResponsePage {
        private List<FetchTripResponseData> items;
        private Pagination pagination;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @SuperBuilder
    public static class FetchTripResponseData {
        private String id;
        private String creator;
        private String tripCode;
        private String merchantId;
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
        private OffsetDateTime departureTime;
        private String rawDepartureDate;
        private String rawDepartureTime;
        private String rawArrivalTime;
        private TripStatus status;
        private String vehicleId;
        private String vehiclePlate;
        private Boolean hasFloor;
        private OffsetDateTime assignedAt;
        private Long availableSeats;
        private BigDecimal ticketPrice;
        private List<SearchTripResponse.SearchRoutePoints> routePoints;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @SuperBuilder
    public static class Pagination {
        private int pageNumber;
        private int pageSize;
        private long totalElements;
        private int totalPages;
    }
}
