package platform.management.service.infrastructure.integration.merchantplatform.client;

import platform.merchant.service.domain.merchant.ApplicationFormStatus;
import platform.management.service.infrastructure.integration.merchantplatform.model.MerchantPlatformFetchMerchantsRequest;
import platform.management.service.infrastructure.integration.merchantplatform.model.MerchantPlatformInternalModels;
import platform.management.service.infrastructure.integration.merchantplatform.model.MerchantPlatformUpdateMerchantRequest;
import platform.core.common.service.api.BaseResponse;

import java.util.List;

public interface MerchantPlatformInternalClient {

    BaseResponse<MerchantPlatformInternalModels.MerchantData> fetchMerchantDetail(String merchantId);

    BaseResponse<MerchantPlatformInternalModels.MerchantPage> fetchMerchants(
            int pageNumber,
            int pageSize
    );

    BaseResponse<List<MerchantPlatformInternalModels.MerchantData>> fetchMerchantsByIds(
            MerchantPlatformFetchMerchantsRequest request
    );

    BaseResponse<List<String>> searchMerchantIds(String merchantName);

    BaseResponse<MerchantPlatformInternalModels.MerchantData> updateMerchant(
            MerchantPlatformUpdateMerchantRequest request
    );

    BaseResponse<MerchantPlatformInternalModels.MerchantApplicationFormPage> fetchApplicationForms(
            ApplicationFormStatus status,
            int pageNumber,
            int pageSize
    );

    BaseResponse<MerchantPlatformInternalModels.MerchantApplicationFormData> fetchApplicationFormDetail(
            String applicationFormId
    );
}
