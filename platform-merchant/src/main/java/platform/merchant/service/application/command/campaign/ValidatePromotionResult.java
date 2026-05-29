package platform.merchant.service.application.command.campaign;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record ValidatePromotionResult(
        String campaignId,
        String promotionCode,
        boolean valid,
        BigDecimal discountAmount,
        BigDecimal finalAmount,
        String message
) {}
