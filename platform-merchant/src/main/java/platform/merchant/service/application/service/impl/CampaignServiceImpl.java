package platform.merchant.service.application.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import platform.core.common.service.application.command.common.PagedResult;
import platform.core.common.service.persistence.exception.BusinessException;
import platform.core.common.service.persistence.utils.ExceptionUtils;
import platform.merchant.service.application.command.campaign.ApplyPromotionCommand;
import platform.merchant.service.application.command.campaign.ApplyPromotionResult;
import platform.merchant.service.application.command.campaign.CreateCampaignCommand;
import platform.merchant.service.application.command.campaign.CreateCampaignResult;
import platform.merchant.service.application.command.campaign.ValidatePromotionCommand;
import platform.merchant.service.application.command.campaign.ValidatePromotionResult;
import platform.merchant.service.application.query.campaign.FetchCampaignsQuery;
import platform.merchant.service.application.query.campaign.FetchCampaignsResult;
import platform.merchant.service.application.service.CampaignService;
import platform.merchant.service.domain.campaign.CampaignStatus;
import platform.merchant.service.domain.campaign.model.Campaign;
import platform.merchant.service.domain.campaign.port.CampaignRepositoryPort;

import java.math.BigDecimal;
import java.util.UUID;

import static platform.core.common.service.persistence.constant.ErrorConstant.DUPLICATE_ERROR;
import static platform.core.common.service.persistence.constant.ErrorConstant.RECORD_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class CampaignServiceImpl implements CampaignService {

    private final CampaignRepositoryPort campaignRepositoryPort;

    @Override
    @Transactional
    public CreateCampaignResult createCampaign(CreateCampaignCommand command) {
        // Check if promotion code already exists
        if (campaignRepositoryPort.findByPromotionCode(command.promotionCode()).isPresent()) {
            throw new BusinessException(ExceptionUtils.buildResultResponse(DUPLICATE_ERROR, "Promotion code already exists: " + command.promotionCode()));
        }

        Campaign campaign = Campaign.builder()
                .id(UUID.randomUUID().toString())
                .merchantId(command.merchantId())
                .name(command.name())
                .description(command.description())
                .promotionCode(command.promotionCode())
                .discountType(command.discountType())
                .discountValue(command.discountValue())
                .maxDiscountAmount(command.maxDiscountAmount())
                .minOrderAmount(command.minOrderAmount())
                .startDate(command.startDate())
                .endDate(command.endDate())
                .usageLimit(command.usageLimit())
                .usedCount(0)
                .status(CampaignStatus.ACTIVE)
                .createdBy(command.creator())
                .build();

        Campaign saved = campaignRepositoryPort.save(campaign);
        return CreateCampaignResult.builder()
                .id(saved.getId())
                .promotionCode(saved.getPromotionCode())
                .build();
    }

    @Override
    @Transactional
    public ApplyPromotionResult applyPromotion(ApplyPromotionCommand command) {
        Campaign campaign = campaignRepositoryPort.findByPromotionCode(command.promotionCode())
                .orElseThrow(() -> new BusinessException(ExceptionUtils.buildResultResponse(RECORD_NOT_FOUND, "Promotion code not found: " + command.promotionCode())));

        // Check if campaign belongs to merchant
        if (!campaign.getMerchantId().equals(command.merchantId())) {
             throw new BusinessException(ExceptionUtils.buildResultResponse(RECORD_NOT_FOUND, "Promotion code not valid for this merchant"));
        }

        if (!campaign.isAvailable()) {
            throw new BusinessException(ExceptionUtils.buildResultResponse("CAMPAIGN_NOT_AVAILABLE", "Promotion code is not available or expired"));
        }

        BigDecimal discountAmount = campaign.calculateDiscount(command.orderAmount());
        if (discountAmount.compareTo(BigDecimal.ZERO) == 0) {
             throw new BusinessException(ExceptionUtils.buildResultResponse("INVALID_PROMOTION", "Promotion conditions not met (e.g. min order amount)"));
        }

        // Increment used count
        campaign.setUsedCount(campaign.getUsedCount() + 1);
        campaignRepositoryPort.save(campaign);

        return ApplyPromotionResult.builder()
                .campaignId(campaign.getId())
                .promotionCode(campaign.getPromotionCode())
                .discountAmount(discountAmount)
                .finalAmount(command.orderAmount().subtract(discountAmount))
                .build();
    }

    @Override
    public ValidatePromotionResult validatePromotion(ValidatePromotionCommand command) {
        Campaign campaign = campaignRepositoryPort.findByPromotionCode(command.promotionCode()).orElse(null);

        if (campaign == null) {
            return ValidatePromotionResult.builder()
                    .valid(false)
                    .message("Promotion code not found")
                    .build();
        }

        if (!campaign.getMerchantId().equals(command.merchantId())) {
            return ValidatePromotionResult.builder()
                    .valid(false)
                    .message("Promotion code not valid for this merchant")
                    .build();
        }

        if (!campaign.isAvailable()) {
            return ValidatePromotionResult.builder()
                    .valid(false)
                    .message("Promotion code is not available or expired")
                    .build();
        }

        BigDecimal discountAmount = campaign.calculateDiscount(command.orderAmount());
        if (discountAmount.compareTo(BigDecimal.ZERO) == 0) {
            return ValidatePromotionResult.builder()
                    .valid(false)
                    .message("Promotion conditions not met (e.g. min order amount)")
                    .build();
        }

        return ValidatePromotionResult.builder()
                .campaignId(campaign.getId())
                .promotionCode(campaign.getPromotionCode())
                .valid(true)
                .discountAmount(discountAmount)
                .finalAmount(command.orderAmount().subtract(discountAmount))
                .message("Valid")
                .build();
    }

    @Override
    public FetchCampaignsResult fetchCampaigns(FetchCampaignsQuery query) {
        PagedResult<Campaign> pagedResult = campaignRepositoryPort.findByMerchantId(
                query.merchantId(),
                query.pageNumber() - 1,
                query.pageSize()
        );

        return FetchCampaignsResult.builder()
                .items(pagedResult.getItems())
                .totalElements(pagedResult.getTotalElements())
                .totalPages(pagedResult.getTotalPages())
                .pageNumber(pagedResult.getPageNumber() + 1)
                .pageSize(pagedResult.getPageSize())
                .build();
    }
}
