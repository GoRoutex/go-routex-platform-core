package platform.merchant.service.interfaces.model.route;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import platform.core.common.service.api.BaseResponse;

import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class SearchRouteResponse extends BaseResponse<List<SearchRouteResponse.SearchRouteResponseData>> {
    @Getter
    @Setter
    @NoArgsConstructor
    @SuperBuilder
    public static class SearchRouteResponseData {
        private String id;
        private String pickupBranch;
        private String origin;
        private String destination;
        private Long availableSeats;
        private OffsetDateTime plannedStartTime;
        private OffsetDateTime plannedEndTime;
        private String vehiclePlate;
        private boolean hasFloor;
        private String routeCode;
        private List<SearchRoutePoints> routePoints;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    public static class SearchRoutePoints {
        private String id;
        private String routeId;
        private String creator;
        private int stopOrder;
        private String note;
        private String departmentId;
        private String stopName;
        private String stopAddress;
        private String stopCity;
        private Double stopLatitude;
        private Double stopLongitude;
        private Integer timeAtDepartment;
        private Long stayDuration;
        private OffsetDateTime createdAt;
        private String createdBy;
    }
}
