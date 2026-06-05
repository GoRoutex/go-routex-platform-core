package platform.merchant.service.interfaces.model.route;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import platform.core.common.service.api.BaseResponse;
import platform.merchant.service.domain.route.RouteStatus;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class CreateRouteResponse extends BaseResponse<CreateRouteResponse.CreateRouteResponseData> {

    @Getter
    @Setter
    @NoArgsConstructor
    @SuperBuilder
    public static class CreateRouteResponseData {
        private String id;
        private String creator;
        private String originCode;
        private String originName;
        private String originDepartmentId;
        private String originDepartmentName;
        private String destinationCode;
        private String destinationName;
        private String destinationDepartmentId;
        private String destinationDepartmentName;
        private Long duration;
        private RouteStatus status;
        private List<CreateRouteRequest.RoutePoints> routePoints;
    }
}
