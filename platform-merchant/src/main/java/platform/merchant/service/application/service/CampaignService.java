package platform.merchant.service.application.service;


import platform.merchant.service.application.command.campaign.ApplyPromotionCommand;
import platform.merchant.service.application.command.campaign.ApplyPromotionResult;
import platform.merchant.service.application.command.campaign.CreateCampaignCommand;
import platform.merchant.service.application.command.campaign.CreateCampaignResult;
import platform.merchant.service.application.command.campaign.ValidatePromotionCommand;
import platform.merchant.service.application.command.campaign.ValidatePromotionResult;
import platform.merchant.service.application.query.campaign.FetchCampaignsQuery;
import platform.merchant.service.application.query.campaign.FetchCampaignsResult;

public interface CampaignService {
    CreateCampaignResult createCampaign(CreateCampaignCommand command);
    ApplyPromotionResult applyPromotion(ApplyPromotionCommand command);
    ValidatePromotionResult validatePromotion(ValidatePromotionCommand command);
    FetchCampaignsResult fetchCampaigns(FetchCampaignsQuery query);
}
