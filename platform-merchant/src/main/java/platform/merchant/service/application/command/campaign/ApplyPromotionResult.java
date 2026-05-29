package platform.merchant.service.application.command.campaign;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record ApplyPromotionResult(
        String campaignId,
        String promotionCode,
        BigDecimal discountAmount,
        BigDecimal finalAmount
) {}
