package platform.merchant.service.interfaces.model.assignment;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import platform.core.common.service.api.BaseResponse;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class AssignRouteResponse extends BaseResponse<AssignRouteResponse.AssignRouteResponseData> {

    @Getter
    @Setter
    @NoArgsConstructor
    @SuperBuilder
    public static class AssignRouteResponseData {
        private String creator;
        private String tripId;
        private String vehicleId;
        private String assignedAt;
        private String status;
    }
}
