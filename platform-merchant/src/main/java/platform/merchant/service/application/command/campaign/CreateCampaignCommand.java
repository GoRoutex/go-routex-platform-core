package platform.merchant.service.application.command.campaign;

import lombok.Builder;
import platform.core.common.service.common.RequestContext;
import platform.merchant.service.domain.campaign.DiscountType;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Builder
public record CreateCampaignCommand(
        RequestContext context,
        String merchantId,
        String name,
        String description,
        String promotionCode,
        DiscountType discountType,
        BigDecimal discountValue,
        BigDecimal maxDiscountAmount,
        BigDecimal minOrderAmount,
        OffsetDateTime startDate,
        OffsetDateTime endDate,
        Integer usageLimit,
        String creator
) {}
