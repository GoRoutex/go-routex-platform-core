package platform.merchant.service.interfaces.model.merchant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import platform.core.common.service.api.BaseResponse;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class RejectMerchantApplicationResponse extends BaseResponse<RejectMerchantApplicationResponse.RejectMerchantApplicationResponseData> {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    public static class RejectMerchantApplicationResponseData {
        private String applicationId;
        private String formCode;
        private String status;
        private String rejectedBy;
        private String rejectionReason;
    }
}
