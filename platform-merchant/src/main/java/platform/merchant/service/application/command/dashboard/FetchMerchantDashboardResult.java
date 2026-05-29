package platform.merchant.service.application.command.dashboard;

import lombok.Builder;
import platform.merchant.service.interfaces.model.dashboard.response.MerchantDashboardResponse;

@Builder
public record FetchMerchantDashboardResult(
        MerchantDashboardResponse data
) {}
