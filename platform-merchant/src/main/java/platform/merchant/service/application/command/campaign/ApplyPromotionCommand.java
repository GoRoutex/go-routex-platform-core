package platform.merchant.service.application.command.campaign;

import lombok.Builder;
import platform.core.common.service.common.RequestContext;

import java.math.BigDecimal;

@Builder
public record ApplyPromotionCommand(
        RequestContext context,
        String merchantId,
        String promotionCode,
        BigDecimal orderAmount
) {}
