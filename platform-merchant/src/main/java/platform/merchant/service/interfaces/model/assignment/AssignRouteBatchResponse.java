package platform.merchant.service.interfaces.model.assignment;

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
public class AssignRouteBatchResponse extends BaseResponse<AssignRouteBatchResponse.AssignRouteBatchResponseData> {

    @Getter
    @Setter
    @NoArgsConstructor
    @SuperBuilder
    public static class AssignRouteBatchResponseData {
        private int successCount;
        private int failedCount;
        private List<SuccessItem> successItems;
        private List<FailedItem> failedItems;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @SuperBuilder
    public static class SuccessItem {
        private String tripId;
        private String vehicleId;
        private String driverId;
        private String assignedAt;
        private String status;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @SuperBuilder
    public static class FailedItem {
        private String tripId;
        private String driverId;
        private String vehicleId;
        private String errorCode;
        private String errorMessage;
    }
}
