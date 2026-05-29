package platform.merchant.service.interfaces.model.campaign.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import platform.core.common.service.api.BaseRequest;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
public class ValidatePromotionRequest extends BaseRequest {
    private ValidatePromotionData data;

    @Getter
    @Setter
    public static class ValidatePromotionData {
        @NotBlank(message = "Promotion code is required")
        private String promotionCode;
        @NotNull(message = "Order amount is required")
        private BigDecimal orderAmount;
        @NotBlank(message = "Merchant ID is required")
        private String merchantId;
    }
}
