package platform.merchant.service.application.command.campaign;

import lombok.Builder;

@Builder
public record CreateCampaignResult(
        String id,
        String promotionCode
) {}
