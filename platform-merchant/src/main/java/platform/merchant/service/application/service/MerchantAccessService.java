package platform.merchant.service.application.service;

import platform.merchant.service.application.command.merchant.GetMyMerchantCommand;
import platform.merchant.service.application.command.merchant.GetMyMerchantResult;

public interface MerchantAccessService {
    GetMyMerchantResult fetchMerchantDetail(GetMyMerchantCommand build);
}
