package platform.management.service.application.services;

import platform.management.service.application.command.merchant.FetchMerchantDetailQuery;
import platform.management.service.application.command.merchant.FetchMerchantDetailResult;
import platform.management.service.application.command.merchant.FetchMerchantsQuery;
import platform.management.service.application.command.merchant.FetchMerchantsResult;
import platform.management.service.application.command.merchant.UpdateMerchantCommand;
import platform.management.service.application.command.merchant.UpdateMerchantResult;

public interface MerchantManagementService {
    FetchMerchantDetailResult fetchMerchantDetail(FetchMerchantDetailQuery query);

    FetchMerchantsResult fetchMerchants(FetchMerchantsQuery query);

    UpdateMerchantResult updateMerchant(UpdateMerchantCommand command);
}
