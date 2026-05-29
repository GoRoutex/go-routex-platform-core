package platform.merchant.service.domain.campaign.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import platform.core.common.service.persistence.AbstractAuditingEntity;
import platform.merchant.service.domain.campaign.CampaignStatus;
import platform.merchant.service.domain.campaign.DiscountType;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Campaign extends AbstractAuditingEntity {
    private String id;
    private String merchantId;
    private String name;
    private String description;
    private String promotionCode;
    private DiscountType discountType;
    private BigDecimal discountValue;
    private BigDecimal maxDiscountAmount;
    private BigDecimal minOrderAmount;
    private OffsetDateTime startDate;
    private OffsetDateTime endDate;
    private Integer usageLimit;
    private Integer usedCount;
    private CampaignStatus status;

    public boolean isAvailable() {
        OffsetDateTime now = OffsetDateTime.now();
        return status == CampaignStatus.ACTIVE &&
                (startDate == null || now.isAfter(startDate)) &&
                (endDate == null || now.isBefore(endDate)) &&
                (usageLimit == null || usedCount < usageLimit);
    }

    public BigDecimal calculateDiscount(BigDecimal orderAmount) {
        if (!isAvailable()) {
            return BigDecimal.ZERO;
        }

        if (minOrderAmount != null && orderAmount.compareTo(minOrderAmount) < 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal discount = BigDecimal.ZERO;
        if (discountType == DiscountType.FIXED_AMOUNT) {
            discount = discountValue;
        } else if (discountType == DiscountType.PERCENTAGE) {
            discount = orderAmount.multiply(discountValue).divide(new BigDecimal("100"));
            if (maxDiscountAmount != null && discount.compareTo(maxDiscountAmount) > 0) {
                discount = maxDiscountAmount;
            }
        }

        // Discount cannot exceed order amount
        return discount.min(orderAmount);
    }
}
