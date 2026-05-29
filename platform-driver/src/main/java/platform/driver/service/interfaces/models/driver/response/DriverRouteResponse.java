package platform.driver.service.interfaces.models.driver.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import platform.core.common.service.api.BaseResponse;

@Getter
@Setter
@SuperBuilder
public class DriverRouteResponse extends BaseResponse<DriverRouteResponse.DriverRouteResponseData> {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    public static class DriverRouteResponseData {
        private String driverId;
        private String currentRouteId;
    }
}
