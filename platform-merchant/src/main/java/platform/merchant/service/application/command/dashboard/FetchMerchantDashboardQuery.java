package platform.merchant.service.application.command.dashboard;

import lombok.Builder;
import platform.core.common.service.common.RequestContext;

@Builder
public record FetchMerchantDashboardQuery(
        String merchantId,
        String filterType, // DAY, WEEK, MONTH, YEAR
        RequestContext context
) {}
