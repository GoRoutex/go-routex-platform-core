package platform.merchant.service.application.service;

import platform.core.common.service.common.RequestContext;
import platform.core.common.service.application.command.common.PagedResult;
import platform.merchant.service.domain.merchant.ApplicationFormStatus;
import platform.merchant.service.domain.merchant.model.Merchant;
import platform.merchant.service.domain.merchant.model.MerchantApplicationForm;
import platform.merchant.service.interfaces.model.internal.merchant.InternalUpdateMerchantRequest;

import java.util.List;

public interface InternalMerchantAdminService {

    Merchant fetchMerchantDetail(String merchantId, RequestContext context);

    PagedResult<Merchant> fetchMerchants(RequestContext context, int pageNumber, int pageSize);

    PagedResult<Merchant> fetchMerchants(RequestContext context, String merchantName, int pageNumber, int pageSize);

    List<Merchant> fetchMerchantsByIds(List<String> merchantIds, RequestContext context);

    List<String> findMerchantIdsByName(String merchantName, RequestContext context);

    Merchant updateMerchant(InternalUpdateMerchantRequest request, RequestContext context);

    PagedResult<MerchantApplicationForm> fetchApplicationForms(RequestContext context, ApplicationFormStatus status, int pageNumber, int pageSize);

    MerchantApplicationForm fetchApplicationFormDetail(String applicationFormId, RequestContext context);
}
