package platform.merchant.service.interfaces.model.merchant;


import lombok.AllArgsConstructor;
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
public class ReviewMerchantResponse extends BaseResponse<ReviewMerchantResponse.ReviewMerchantResponseData> {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    public static class ReviewMerchantResponseData {
        private String merchantId;
        private String status;
        private String approvedBy;
        private OffsetDateTime approvedAt;
        private String rejectionReason;
    }
}
