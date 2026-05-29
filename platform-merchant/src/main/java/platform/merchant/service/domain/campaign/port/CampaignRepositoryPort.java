package platform.merchant.service.domain.campaign.port;

import platform.merchant.service.domain.campaign.model.Campaign;
import platform.core.common.service.application.command.common.PagedResult;

import java.util.Optional;

public interface CampaignRepositoryPort {
    Campaign save(Campaign campaign);
    Optional<Campaign> findById(String id);
    Optional<Campaign> findByPromotionCode(String promotionCode);
    PagedResult<Campaign> findByMerchantId(String merchantId, int page, int size);
    void deleteById(String id);
}
