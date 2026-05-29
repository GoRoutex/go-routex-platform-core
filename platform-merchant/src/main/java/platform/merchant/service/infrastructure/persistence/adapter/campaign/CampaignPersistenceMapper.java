package platform.merchant.service.infrastructure.persistence.adapter.campaign;

import org.springframework.stereotype.Component;
import platform.merchant.service.domain.campaign.model.Campaign;
import platform.merchant.service.infrastructure.persistence.jpa.campaign.entity.CampaignEntity;

@Component
public class CampaignPersistenceMapper {

    public Campaign toDomain(CampaignEntity entity) {
        if (entity == null) return null;
        return Campaign.builder()
                .id(entity.getId())
                .merchantId(entity.getMerchantId())
                .name(entity.getName())
                .description(entity.getDescription())
                .promotionCode(entity.getPromotionCode())
                .discountType(entity.getDiscountType())
                .discountValue(entity.getDiscountValue())
                .maxDiscountAmount(entity.getMaxDiscountAmount())
                .minOrderAmount(entity.getMinOrderAmount())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .usageLimit(entity.getUsageLimit())
                .usedCount(entity.getUsedCount())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedAt(entity.getUpdatedAt())
                .updatedBy(entity.getUpdatedBy())
                .build();
    }

    public CampaignEntity toEntity(Campaign domain) {
        if (domain == null) return null;
        return CampaignEntity.builder()
                .id(domain.getId())
                .merchantId(domain.getMerchantId())
                .name(domain.getName())
                .description(domain.getDescription())
                .promotionCode(domain.getPromotionCode())
                .discountType(domain.getDiscountType())
                .discountValue(domain.getDiscountValue())
                .maxDiscountAmount(domain.getMaxDiscountAmount())
                .minOrderAmount(domain.getMinOrderAmount())
                .startDate(domain.getStartDate())
                .endDate(domain.getEndDate())
                .usageLimit(domain.getUsageLimit())
                .usedCount(domain.getUsedCount())
                .status(domain.getStatus())
                .createdAt(domain.getCreatedAt())
                .createdBy(domain.getCreatedBy())
                .updatedAt(domain.getUpdatedAt())
                .updatedBy(domain.getUpdatedBy())
                .build();
    }
}
