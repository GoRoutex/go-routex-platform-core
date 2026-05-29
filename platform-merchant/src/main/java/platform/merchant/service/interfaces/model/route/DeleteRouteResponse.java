package platform.merchant.service.interfaces.model.route;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import platform.core.common.service.api.BaseResponse;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class DeleteRouteResponse extends BaseResponse<DeleteRouteResponse.DeleteRouteResponseData> {

    @Getter
    @Setter
    @NoArgsConstructor
    @SuperBuilder
    public static class DeleteRouteResponseData {
        private String creator;
        private String routeId;
        private String status;
        private OffsetDateTime updatedAt;
    }
}
