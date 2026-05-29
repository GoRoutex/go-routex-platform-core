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
public class RegisterMerchantResponse extends BaseResponse<RegisterMerchantResponse.RegisterMerchantResponseData> {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    public static class RegisterMerchantResponseData {
        private String merchantId;
        private String code;
        private String name;
        private String status;
        private OffsetDateTime createdAt;
    }
}
