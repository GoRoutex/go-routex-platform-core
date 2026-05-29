package platform.merchant.service.interfaces.model.campaign.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import platform.core.common.service.api.BaseResponse;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class CreateCampaignResponse extends BaseResponse<CreateCampaignResponse.CreateCampaignResponseData> {

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CreateCampaignResponseData {
        private String id;
        private String promotionCode;
    }
}
