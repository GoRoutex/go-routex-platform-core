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
public class ScheduleAsyncResponse extends BaseResponse<ScheduleAsyncResponse.ScheduleAsyncResponseData> {

    @Getter
    @Setter
    @NoArgsConstructor
    @SuperBuilder
    public static class ScheduleAsyncResponseData {
        private String jobId;
        private String status;
    }
}
