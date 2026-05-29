package platform.merchant.service.infrastructure.persistence.jpa.campaign.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import platform.merchant.service.infrastructure.persistence.jpa.campaign.entity.CampaignEntity;

import java.util.Optional;

@Repository
public interface CampaignJpaRepository extends JpaRepository<CampaignEntity, String> {
    Optional<CampaignEntity> findByPromotionCode(String promotionCode);
    Page<CampaignEntity> findAllByMerchantId(String merchantId, Pageable pageable);
}
