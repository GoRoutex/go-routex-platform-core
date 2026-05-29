package platform.merchant.service.interfaces.model.campaign.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import platform.merchant.service.domain.campaign.DiscountType;
import platform.core.common.service.api.BaseRequest;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
public class CreateCampaignRequest extends BaseRequest {
    private CreateCampaignData data;

    @Getter
    @Setter
    public static class CreateCampaignData {
        @NotBlank(message = "Campaign name is required")
        private String name;
        private String description;
        @NotBlank(message = "Promotion code is required")
        private String promotionCode;
        @NotNull(message = "Discount type is required")
        private DiscountType discountType;
        @NotNull(message = "Discount value is required")
        private BigDecimal discountValue;
        private BigDecimal maxDiscountAmount;
        private BigDecimal minOrderAmount;
        private OffsetDateTime startDate;
        private OffsetDateTime endDate;
        private Integer usageLimit;
        private String creator;
    }
}
