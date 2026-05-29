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
public class AcceptMerchantApplicationResponse extends BaseResponse<AcceptMerchantApplicationResponse.AcceptMerchantApplicationResponseData> {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    public static class AcceptMerchantApplicationResponseData {
        private String applicationId;
        private String formCode;
        private String merchantId;
        private String merchantCode;
        private String merchantName;
        private String status;
        private String approvedBy;
        private OffsetDateTime approvedAt;
    }
}
