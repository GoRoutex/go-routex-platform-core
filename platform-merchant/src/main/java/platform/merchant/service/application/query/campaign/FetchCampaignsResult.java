package platform.merchant.service.application.query.campaign;

import lombok.Builder;
import platform.merchant.service.domain.campaign.model.Campaign;

import java.util.List;

@Builder
public record FetchCampaignsResult(
        List<Campaign> items,
        long totalElements,
        int totalPages,
        int pageNumber,
        int pageSize
) {}
