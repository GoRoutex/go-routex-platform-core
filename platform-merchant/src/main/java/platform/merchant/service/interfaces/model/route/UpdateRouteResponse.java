package platform.merchant.service.interfaces.model.route;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import platform.merchant.service.domain.route.RouteStatus;
import platform.core.common.service.api.BaseResponse;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class UpdateRouteResponse extends BaseResponse<UpdateRouteResponse.UpdateRouteResponseData> {

    private String routeId;
    private String creator;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    public static class UpdateRouteResponseData {
        private String originCode;
        private String originName;
        private String destinationCode;
        private String destinationName;
        private String originDepartmentId;
        private String destinationDepartmentId;
        private Long duration;
        private RouteStatus status;
        private List<UpdateRoutePointResponse> routePoints;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    public static class UpdateRoutePointResponse {
        private int stopOrder;
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
