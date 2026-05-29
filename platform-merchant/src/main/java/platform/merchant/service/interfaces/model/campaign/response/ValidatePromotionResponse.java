package platform.merchant.service.interfaces.model.campaign.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import platform.core.common.service.api.BaseResponse;

import java.math.BigDecimal;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class ValidatePromotionResponse extends BaseResponse<ValidatePromotionResponse.ValidatePromotionResponseData> {

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ValidatePromotionResponseData {
        private String campaignId;
        private String promotionCode;
        private boolean valid;
        private BigDecimal discountAmount;
        private BigDecimal finalAmount;
        private String message;
    }
}
