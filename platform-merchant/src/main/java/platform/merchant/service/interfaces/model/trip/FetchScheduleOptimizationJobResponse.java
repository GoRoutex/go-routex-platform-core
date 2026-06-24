package platform.merchant.service.interfaces.model.trip;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import platform.core.common.service.api.BaseResponse;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class FetchScheduleOptimizationJobResponse extends BaseResponse<FetchScheduleOptimizationJobResponse.FetchScheduleOptimizationJobResponseData> {

    @Getter
    @Setter
    @NoArgsConstructor
    @SuperBuilder
    public static class FetchScheduleOptimizationJobResponseData {
        private String jobId;
        private String merchantId;
        private String routeId;
        private String status;
        private String recommendationsPayload;
        private String creatorEmail;
    }
}
