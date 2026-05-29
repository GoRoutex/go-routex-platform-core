package platform.merchant.service.application.query.campaign;

import lombok.Builder;
import platform.core.common.service.common.RequestContext;

@Builder
public record FetchCampaignsQuery(
        RequestContext context,
        String merchantId,
        int pageNumber,
        int pageSize
) {}
