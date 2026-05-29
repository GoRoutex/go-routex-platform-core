package platform.merchant.service.interfaces.model.campaign.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import platform.merchant.service.domain.campaign.CampaignStatus;
import platform.merchant.service.domain.campaign.DiscountType;
import platform.core.common.service.api.BaseResponse;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class FetchCampaignsResponse extends BaseResponse<FetchCampaignsResponse.FetchCampaignsResponsePage> {

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FetchCampaignsResponsePage {
        private List<FetchCampaignsResponseData> items;
        private Pagination pagination;
    }

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FetchCampaignsResponseData {
        private String id;
        private String name;
        private String description;
        private String promotionCode;
        private DiscountType discountType;
        private BigDecimal discountValue;
        private BigDecimal maxDiscountAmount;
        private BigDecimal minOrderAmount;
        private OffsetDateTime startDate;
        private OffsetDateTime endDate;
        private Integer usageLimit;
        private Integer usedCount;
        private CampaignStatus status;
        private OffsetDateTime createdAt;
    }

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Pagination {
        private int pageNumber;
        private int pageSize;
        private long totalElements;
        private int totalPages;
    }
}
