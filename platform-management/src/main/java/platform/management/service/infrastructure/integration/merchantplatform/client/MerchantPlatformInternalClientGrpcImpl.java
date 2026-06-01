package platform.management.service.infrastructure.integration.merchantplatform.client;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import platform.core.common.service.api.ApiResult;
import platform.core.common.service.api.BaseResponse;
import platform.core.common.service.application.command.common.PagedResult;
import platform.core.common.service.common.RequestAttributes;
import platform.core.common.service.persistence.constant.ErrorConstant;
import platform.merchant.service.application.service.InternalMerchantAdminService;
import platform.merchant.service.domain.merchant.ApplicationFormStatus;
import platform.merchant.service.domain.merchant.model.ApplicationFormBankInfo;
import platform.merchant.service.domain.merchant.model.ApplicationFormContact;
import platform.merchant.service.domain.merchant.model.ApplicationFormOwner;
import platform.merchant.service.domain.merchant.model.Merchant;
import platform.merchant.service.domain.merchant.model.MerchantApplicationForm;
import platform.merchant.service.interfaces.model.internal.merchant.InternalUpdateMerchantRequest;
import platform.management.service.infrastructure.integration.merchantplatform.model.MerchantPlatformFetchMerchantsRequest;
import platform.management.service.infrastructure.integration.merchantplatform.model.MerchantPlatformInternalModels;
import platform.management.service.infrastructure.integration.merchantplatform.model.MerchantPlatformUpdateMerchantRequest;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MerchantPlatformInternalClientGrpcImpl implements MerchantPlatformInternalClient {

    private final InternalMerchantAdminService internalMerchantAdminService;

    @Override
    public BaseResponse<MerchantPlatformInternalModels.MerchantData> fetchMerchantDetail(String merchantId) {
        Merchant merchant = internalMerchantAdminService.fetchMerchantDetail(merchantId, buildRequestContext());
        return successResponse(mapMerchantData(merchant));
    }

    @Override
    public BaseResponse<MerchantPlatformInternalModels.MerchantPage> fetchMerchants(int pageNumber, int pageSize) {
        PagedResult<Merchant> merchantPage = internalMerchantAdminService.fetchMerchants(buildRequestContext(), pageNumber, pageSize);
        MerchantPlatformInternalModels.MerchantPage page = new MerchantPlatformInternalModels.MerchantPage();
        page.setItems(merchantPage.getItems().stream()
                .map(this::mapMerchantData)
                .toList());
        page.setPagination(mapPagination(merchantPage));
        return successResponse(page);
    }

    @Override
    public BaseResponse<List<MerchantPlatformInternalModels.MerchantData>> fetchMerchantsByIds(MerchantPlatformFetchMerchantsRequest request) {
        List<String> merchantIds = request != null && request.getMerchantIds() != null
                ? request.getMerchantIds()
                : List.of();
        List<MerchantPlatformInternalModels.MerchantData> dataList = internalMerchantAdminService
                .fetchMerchantsByIds(merchantIds, buildRequestContext())
                .stream()
                .map(this::mapMerchantData)
                .toList();
        return successResponse(dataList);
    }

    @Override
    public BaseResponse<List<String>> searchMerchantIds(String merchantName) {
        return successResponse(internalMerchantAdminService.findMerchantIdsByName(merchantName, buildRequestContext()));
    }

    @Override
    public BaseResponse<MerchantPlatformInternalModels.MerchantData> updateMerchant(MerchantPlatformUpdateMerchantRequest request) {
        Merchant merchant = internalMerchantAdminService.updateMerchant(mapUpdateMerchantRequest(request), buildRequestContext());
        return successResponse(mapMerchantData(merchant));
    }

    @Override
    public BaseResponse<MerchantPlatformInternalModels.MerchantApplicationFormPage> fetchApplicationForms(
            ApplicationFormStatus status, int pageNumber, int pageSize) {
        PagedResult<MerchantApplicationForm> formPage = internalMerchantAdminService.fetchApplicationForms(
                buildRequestContext(),
                status,
                pageNumber,
                pageSize
        );
        MerchantPlatformInternalModels.MerchantApplicationFormPage page = new MerchantPlatformInternalModels.MerchantApplicationFormPage();
        page.setItems(formPage.getItems().stream()
                .map(this::mapApplicationFormData)
                .toList());
        page.setPagination(mapPagination(formPage));
        return successResponse(page);
    }

    @Override
    public BaseResponse<MerchantPlatformInternalModels.MerchantApplicationFormData> fetchApplicationFormDetail(String applicationFormId) {
        MerchantApplicationForm form = internalMerchantAdminService.fetchApplicationFormDetail(
                applicationFormId,
                buildRequestContext()
        );
        return successResponse(mapApplicationFormData(form));
    }

    private platform.core.common.service.common.RequestContext buildRequestContext() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        String requestId = null;
        String requestDateTime = null;
        String channel = null;
        if (attributes != null) {
            var request = attributes.getRequest();
            requestId = request.getHeader(RequestAttributes.REQUEST_ID);
            requestDateTime = request.getHeader(RequestAttributes.REQUEST_DATE_TIME);
            channel = request.getHeader(RequestAttributes.CHANNEL);
        }
        if (requestId == null || requestId.isBlank()) {
            requestId = UUID.randomUUID().toString();
        }
        if (requestDateTime == null || requestDateTime.isBlank()) {
            requestDateTime = OffsetDateTime.now(ZoneOffset.ofHours(7)).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        }
        if (channel == null || channel.isBlank()) {
            channel = "ONL";
        }
        return platform.core.common.service.common.RequestContext.builder()
                .requestId(requestId)
                .requestDateTime(requestDateTime)
                .channel(channel)
                .build();
    }

    private <T> BaseResponse<T> successResponse(T data) {
        return BaseResponse.<T>builder()
                .result(ApiResult.builder()
                        .responseCode(ErrorConstant.SUCCESS_CODE)
                        .description("Success")
                        .build())
                .data(data)
                .build();
    }

    private MerchantPlatformInternalModels.MerchantData mapMerchantData(Merchant merchant) {
        if (merchant == null) {
            return null;
        }
        MerchantPlatformInternalModels.MerchantData data = new MerchantPlatformInternalModels.MerchantData();
        data.setId(merchant.getId());
        data.setCode(merchant.getCode());
        data.setSlug(merchant.getSlug());
        data.setDisplayName(merchant.getDisplayName());
        data.setLegalName(merchant.getLegalName());
        data.setTaxCode(merchant.getTaxCode());
        data.setBusinessLicenseNumber(merchant.getBusinessLicenseNumber());
        data.setBusinessLicenseUrl(merchant.getBusinessLicenseUrl());
        data.setPhone(merchant.getPhone());
        data.setEmail(merchant.getEmail());
        data.setLogoUrl(merchant.getLogoUrl());
        data.setDescription(merchant.getDescription());
        data.setAddress(merchant.getAddress());
        data.setWard(merchant.getWard());
        data.setProvince(merchant.getProvince());
        data.setCountry(merchant.getCountry());
        data.setPostalCode(merchant.getPostalCode());
        data.setRepresentativeName(merchant.getRepresentativeName());
        data.setContactName(merchant.getContactName());
        data.setContactPhone(merchant.getContactPhone());
        data.setContactEmail(merchant.getContactEmail());
        data.setOwnerFullName(merchant.getOwnerFullName());
        data.setOwnerPhone(merchant.getOwnerPhone());
        data.setOwnerEmail(merchant.getOwnerEmail());
        data.setBankAccountName(merchant.getBankAccountName());
        data.setBankAccountNumber(merchant.getBankAccountNumber());
        data.setBankName(merchant.getBankName());
        data.setBankBranch(merchant.getBankBranch());
        data.setCommissionRate(merchant.getCommissionRate());
        data.setStatus(merchant.getStatus());
        data.setApprovedAt(merchant.getApprovedAt());
        data.setApprovedBy(merchant.getApprovedBy());
        return data;
    }

    private MerchantPlatformInternalModels.MerchantApplicationFormData mapApplicationFormData(MerchantApplicationForm form) {
        if (form == null) {
            return null;
        }
        ApplicationFormContact contact = form.getContact();
        ApplicationFormBankInfo bankInfo = form.getBankInfo();
        ApplicationFormOwner ownerInfo = form.getOwnerInfo();
        MerchantPlatformInternalModels.MerchantApplicationFormData data = new MerchantPlatformInternalModels.MerchantApplicationFormData();
        data.setId(form.getId());
        data.setFormCode(form.getFormCode());
        data.setDisplayName(form.getDisplayName());
        data.setLegalName(form.getLegalName());
        data.setTaxCode(form.getTaxCode());
        data.setBusinessLicense(form.getBusinessLicense());
        data.setBusinessLicenseUrl(form.getBusinessLicenseUrl());
        data.setLogoUrl(form.getLogoUrl());
        data.setDescription(form.getDescription());
        data.setSlug(form.getSlug());
        data.setSubmittedBy(form.getSubmittedBy());
        data.setSubmittedAt(form.getSubmittedAt());
        data.setApprovedBy(form.getApprovedBy());
        data.setApprovedAt(form.getApprovedAt());
        data.setRejectedBy(form.getRejectedBy());
        data.setRejectionReason(form.getRejectionReason());
        data.setCountry(form.getCountry());
        data.setProvince(form.getProvince());
        data.setWard(form.getWard());
        data.setAddress(form.getAddress());
        data.setPostalCode(form.getPostalCode());
        data.setContactName(contact == null ? null : contact.getContactName());
        data.setContactPhone(contact == null ? null : contact.getContactPhone());
        data.setContactEmail(contact == null ? null : contact.getContactEmail());
        data.setOwnerName(ownerInfo == null ? null : ownerInfo.getOwnerName());
        data.setOwnerFullName(ownerInfo == null ? null : ownerInfo.getOwnerFullName());
        data.setOwnerPhone(ownerInfo == null ? null : ownerInfo.getOwnerPhone());
        data.setOwnerEmail(ownerInfo == null ? null : ownerInfo.getOwnerEmail());
        data.setBankAccountName(bankInfo == null ? null : bankInfo.getBankAccountName());
        data.setBankAccountNumber(bankInfo == null ? null : bankInfo.getBankAccountNumber());
        data.setBankName(bankInfo == null ? null : bankInfo.getBankName());
        data.setBankBranch(bankInfo == null ? null : bankInfo.getBankBranch());
        data.setStatus(form.getStatus());
        return data;
    }

    private MerchantPlatformInternalModels.Pagination mapPagination(PagedResult<?> page) {
        if (page == null) {
            return null;
        }
        MerchantPlatformInternalModels.Pagination pagination = new MerchantPlatformInternalModels.Pagination();
        pagination.setPageNumber(page.getPageNumber());
        pagination.setPageSize(page.getPageSize());
        pagination.setTotalElements(page.getTotalElements());
        pagination.setTotalPages(page.getTotalPages());
        return pagination;
    }

    private InternalUpdateMerchantRequest mapUpdateMerchantRequest(MerchantPlatformUpdateMerchantRequest request) {
        InternalUpdateMerchantRequest internalRequest = new InternalUpdateMerchantRequest();
        internalRequest.setMerchantId(request.getMerchantId());
        internalRequest.setCode(request.getCode());
        internalRequest.setSlug(request.getSlug());
        internalRequest.setDisplayName(request.getDisplayName());
        internalRequest.setLegalName(request.getLegalName());
        internalRequest.setTaxCode(request.getTaxCode());
        internalRequest.setBusinessLicenseNumber(request.getBusinessLicenseNumber());
        internalRequest.setBusinessLicenseUrl(request.getBusinessLicenseUrl());
        internalRequest.setPhone(request.getPhone());
        internalRequest.setEmail(request.getEmail());
        internalRequest.setLogoUrl(request.getLogoUrl());
        internalRequest.setDescription(request.getDescription());
        internalRequest.setAddress(request.getAddress());
        internalRequest.setWard(request.getWard());
        internalRequest.setProvince(request.getProvince());
        internalRequest.setCountry(request.getCountry());
        internalRequest.setPostalCode(request.getPostalCode());
        internalRequest.setRepresentativeName(request.getRepresentativeName());
        internalRequest.setContactName(request.getContactName());
        internalRequest.setContactPhone(request.getContactPhone());
        internalRequest.setContactEmail(request.getContactEmail());
        internalRequest.setOwnerFullName(request.getOwnerFullName());
        internalRequest.setOwnerPhone(request.getOwnerPhone());
        internalRequest.setOwnerEmail(request.getOwnerEmail());
        internalRequest.setBankAccountName(request.getBankAccountName());
        internalRequest.setBankAccountNumber(request.getBankAccountNumber());
        internalRequest.setBankName(request.getBankName());
        internalRequest.setBankBranch(request.getBankBranch());
        internalRequest.setCommissionRate(request.getCommissionRate());
        internalRequest.setStatus(request.getStatus());
        internalRequest.setApprovedAt(request.getApprovedAt());
        internalRequest.setApprovedBy(request.getApprovedBy());
        internalRequest.setUpdatedBy(request.getUpdatedBy());
        return internalRequest;
    }
}
