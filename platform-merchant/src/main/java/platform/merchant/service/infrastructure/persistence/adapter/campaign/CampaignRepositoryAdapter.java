package platform.merchant.service.infrastructure.persistence.adapter.campaign;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import platform.core.common.service.application.command.common.PagedResult;
import platform.merchant.service.domain.campaign.model.Campaign;
import platform.merchant.service.domain.campaign.port.CampaignRepositoryPort;
import platform.merchant.service.infrastructure.persistence.jpa.campaign.entity.CampaignEntity;
import platform.merchant.service.infrastructure.persistence.jpa.campaign.repository.CampaignJpaRepository;

import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CampaignRepositoryAdapter implements CampaignRepositoryPort {

    private final CampaignJpaRepository campaignJpaRepository;
    private final CampaignPersistenceMapper mapper;

    @Override
    public Campaign save(Campaign campaign) {
        CampaignEntity entity = mapper.toEntity(campaign);
        CampaignEntity saved = campaignJpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Campaign> findById(String id) {
        return campaignJpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Campaign> findByPromotionCode(String promotionCode) {
        return campaignJpaRepository.findByPromotionCode(promotionCode).map(mapper::toDomain);
    }

    @Override
    public PagedResult<Campaign> findByMerchantId(String merchantId, int page, int size) {
        Page<CampaignEntity> entityPage = campaignJpaRepository.findAllByMerchantId(merchantId, PageRequest.of(page, size));
        return PagedResult.<Campaign>builder()
                .items(entityPage.getContent().stream().map(mapper::toDomain).collect(Collectors.toList()))
                .totalElements(entityPage.getTotalElements())
                .totalPages(entityPage.getTotalPages())
                .pageNumber(entityPage.getNumber())
                .pageSize(entityPage.getSize())
                .build();
    }

    @Override
    public void deleteById(String id) {
        campaignJpaRepository.deleteById(id);
    }
}
