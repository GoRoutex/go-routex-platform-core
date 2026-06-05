package platform.merchant.service.application.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import platform.core.common.service.application.command.common.PagedResult;
import platform.core.common.service.common.RequestContext;
import platform.core.common.service.persistence.exception.BusinessException;
import platform.core.common.service.persistence.utils.ApiRequestUtils;
import platform.core.common.service.persistence.utils.ExceptionUtils;
import platform.merchant.service.application.service.InternalMerchantAdminService;
import platform.merchant.service.domain.merchant.ApplicationFormStatus;
import platform.merchant.service.domain.merchant.model.Merchant;
import platform.merchant.service.domain.merchant.model.MerchantApplicationForm;
import platform.merchant.service.domain.merchant.port.MerchantApplicationFormRepositoryPort;
import platform.merchant.service.domain.merchant.port.MerchantRepositoryPort;
import platform.merchant.service.interfaces.model.internal.merchant.InternalUpdateMerchantRequest;

import java.math.BigDecimal;
import java.util.List;

import static platform.core.common.service.persistence.constant.ErrorConstant.INVALID_COMMISSION_RATE;
import static platform.core.common.service.persistence.constant.ErrorConstant.INVALID_INPUT_ERROR;
import static platform.core.common.service.persistence.constant.ErrorConstant.INVALID_PAGE_NUMBER;
import static platform.core.common.service.persistence.constant.ErrorConstant.INVALID_PAGE_SIZE;
import static platform.core.common.service.persistence.constant.ErrorConstant.MERCHANT_APPLICATION_FORM_NOT_FOUND;
import static platform.core.common.service.persistence.constant.ErrorConstant.MERCHANT_NOT_FOUND_BY_ID;
import static platform.core.common.service.persistence.constant.ErrorConstant.RECORD_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class InternalMerchantAdminServiceImpl implements InternalMerchantAdminService {

    private final MerchantRepositoryPort merchantRepositoryPort;
    private final MerchantApplicationFormRepositoryPort merchantApplicationFormRepositoryPort;

    @Override
    public Merchant fetchMerchantDetail(String merchantId, RequestContext context) {
        return findMerchant(merchantId, context);
    }

    @Override
    public PagedResult<Merchant> fetchMerchants(RequestContext context, int pageNumber, int pageSize) {
        return fetchMerchants(context, null, pageNumber, pageSize);
    }

    @Override
    public PagedResult<Merchant> fetchMerchants(RequestContext context, String merchantName, int pageNumber, int pageSize) {
        validatePaging(pageNumber, pageSize, context);
        return merchantRepositoryPort.fetch(merchantName, pageNumber - 1, pageSize);
    }

    @Override
    public List<Merchant> fetchMerchantsByIds(List<String> merchantIds, RequestContext context) {
        return merchantRepositoryPort.findByIds(merchantIds);
    }

    @Override
    public List<String> findMerchantIdsByName(String merchantName, RequestContext context) {
        return merchantRepositoryPort.findIdsByMerchantName(merchantName);
    }

    @Override
    @Transactional
    public Merchant updateMerchant(InternalUpdateMerchantRequest request, RequestContext context) {
        Merchant existing = findMerchant(request.getMerchantId(), context);
        validateCommissionRate(request.getCommissionRate(), context);

        Merchant updated = existing.toBuilder()
                .code(ApiRequestUtils.firstNonBlank(request.getCode(), existing.getCode()))
                .slug(ApiRequestUtils.firstNonBlank(request.getSlug(), existing.getSlug()))
                .displayName(ApiRequestUtils.firstNonBlank(request.getDisplayName(), existing.getDisplayName()))
                .legalName(ApiRequestUtils.firstNonBlank(request.getLegalName(), existing.getLegalName()))
                .taxCode(ApiRequestUtils.firstNonBlank(request.getTaxCode(), existing.getTaxCode()))
                .businessLicenseNumber(ApiRequestUtils.firstNonBlank(request.getBusinessLicenseNumber(), existing.getBusinessLicenseNumber()))
                .businessLicenseUrl(ApiRequestUtils.firstNonBlank(request.getBusinessLicenseUrl(), existing.getBusinessLicenseUrl()))
                .phone(ApiRequestUtils.firstNonBlank(request.getPhone(), existing.getPhone()))
                .email(ApiRequestUtils.firstNonBlank(request.getEmail(), existing.getEmail()))
                .logoUrl(ApiRequestUtils.firstNonBlank(request.getLogoUrl(), existing.getLogoUrl()))
                .description(ApiRequestUtils.firstNonBlank(request.getDescription(), existing.getDescription()))
                .address(ApiRequestUtils.firstNonBlank(request.getAddress(), existing.getAddress()))
                .ward(ApiRequestUtils.firstNonBlank(request.getWard(), existing.getWard()))
                .province(ApiRequestUtils.firstNonBlank(request.getProvince(), existing.getProvince()))
                .country(ApiRequestUtils.firstNonBlank(request.getCountry(), existing.getCountry()))
                .postalCode(ApiRequestUtils.firstNonBlank(request.getPostalCode(), existing.getPostalCode()))
                .representativeName(ApiRequestUtils.firstNonBlank(request.getRepresentativeName(), existing.getRepresentativeName()))
                .contactName(ApiRequestUtils.firstNonBlank(request.getContactName(), existing.getContactName()))
                .contactPhone(ApiRequestUtils.firstNonBlank(request.getContactPhone(), existing.getContactPhone()))
                .contactEmail(ApiRequestUtils.firstNonBlank(request.getContactEmail(), existing.getContactEmail()))
                .ownerFullName(ApiRequestUtils.firstNonBlank(request.getOwnerFullName(), existing.getOwnerFullName()))
                .ownerPhone(ApiRequestUtils.firstNonBlank(request.getOwnerPhone(), existing.getOwnerPhone()))
                .ownerEmail(ApiRequestUtils.firstNonBlank(request.getOwnerEmail(), existing.getOwnerEmail()))
                .bankAccountName(ApiRequestUtils.firstNonBlank(request.getBankAccountName(), existing.getBankAccountName()))
                .bankAccountNumber(ApiRequestUtils.firstNonBlank(request.getBankAccountNumber(), existing.getBankAccountNumber()))
                .bankName(ApiRequestUtils.firstNonBlank(request.getBankName(), existing.getBankName()))
                .bankBranch(ApiRequestUtils.firstNonBlank(request.getBankBranch(), existing.getBankBranch()))
                .commissionRate(request.getCommissionRate() == null ? existing.getCommissionRate() : request.getCommissionRate())
                .status(request.getStatus() == null ? existing.getStatus() : request.getStatus())
                .approvedAt(request.getApprovedAt() == null ? existing.getApprovedAt() : request.getApprovedAt())
                .approvedBy(ApiRequestUtils.firstNonBlank(request.getApprovedBy(), existing.getApprovedBy()))
                .updatedBy(ApiRequestUtils.firstNonBlank(request.getUpdatedBy(), existing.getUpdatedBy()))
                .build();

        return merchantRepositoryPort.save(updated);
    }

    @Override
    public PagedResult<MerchantApplicationForm> fetchApplicationForms(RequestContext context, ApplicationFormStatus status, int pageNumber, int pageSize) {
        validatePaging(pageNumber, pageSize, context);
        return status == null
                ? merchantApplicationFormRepositoryPort.fetch(pageNumber - 1, pageSize)
                : merchantApplicationFormRepositoryPort.fetchByStatus(status, pageNumber - 1, pageSize);
    }

    @Override
    public MerchantApplicationForm fetchApplicationFormDetail(String applicationFormId, RequestContext context) {
        return merchantApplicationFormRepositoryPort.findById(applicationFormId)
                .orElseThrow(() -> new BusinessException(
                        context.requestId(),
                        context.requestDateTime(),
                        context.channel(),
                        ExceptionUtils.buildResultResponse(RECORD_NOT_FOUND, String.format(MERCHANT_APPLICATION_FORM_NOT_FOUND, applicationFormId))
                ));
    }

    private Merchant findMerchant(String merchantId, RequestContext context) {
        return merchantRepositoryPort.findById(merchantId)
                .orElseThrow(() -> new BusinessException(
                        context.requestId(),
                        context.requestDateTime(),
                        context.channel(),
                        ExceptionUtils.buildResultResponse(RECORD_NOT_FOUND, String.format(MERCHANT_NOT_FOUND_BY_ID, merchantId))
                ));
    }

    private void validatePaging(int pageNumber, int pageSize, RequestContext context) {
        if (pageSize < 1 || pageSize > 100) {
            throw new BusinessException(context.requestId(), context.requestDateTime(), context.channel(),
                    ExceptionUtils.buildResultResponse(INVALID_INPUT_ERROR, INVALID_PAGE_SIZE));
        }
        if (pageNumber < 1) {
            throw new BusinessException(context.requestId(), context.requestDateTime(), context.channel(),
                    ExceptionUtils.buildResultResponse(INVALID_INPUT_ERROR, INVALID_PAGE_NUMBER));
        }
    }

    private void validateCommissionRate(BigDecimal commissionRate, RequestContext context) {
        if (commissionRate == null) {
            return;
        }
        if (commissionRate.compareTo(BigDecimal.ZERO) < 0 || commissionRate.compareTo(new BigDecimal("100")) > 0) {
            throw new BusinessException(context.requestId(), context.requestDateTime(), context.channel(),
                    ExceptionUtils.buildResultResponse(INVALID_INPUT_ERROR, INVALID_COMMISSION_RATE));
        }
    }
}
