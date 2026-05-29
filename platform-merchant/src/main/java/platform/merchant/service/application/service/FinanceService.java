package platform.merchant.service.application.service;

import platform.merchant.service.application.command.finance.FetchMerchantRevenueQuery;
import platform.merchant.service.application.command.finance.FetchSystemRevenueQuery;
import platform.merchant.service.interfaces.model.finance.response.MerchantRevenueResponse;
import platform.merchant.service.interfaces.model.finance.response.SystemRevenueResponse;

public interface FinanceService {
    MerchantRevenueResponse getMerchantRevenue(FetchMerchantRevenueQuery query);
    SystemRevenueResponse getSystemRevenue(FetchSystemRevenueQuery query);
}

