package platform.merchant.service.interfaces.model.trip;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import platform.core.common.service.api.BaseResponse;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class CreateTripBatchResponse extends BaseResponse<CreateTripBatchResponse.CreateTripBatchResponseData> {


    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    public static class CreateTripBatchResponseData {
        private String routeId;
        private List<String> tripIds;
    }
}
