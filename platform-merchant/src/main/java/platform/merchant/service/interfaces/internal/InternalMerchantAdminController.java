package platform.merchant.service.interfaces.internal;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import platform.core.common.service.api.BaseRequest;
import platform.core.common.service.api.BaseResponse;
import platform.core.common.service.application.command.common.PagedResult;
import platform.core.common.service.persistence.utils.ApiRequestUtils;
import platform.merchant.service.application.service.InternalMerchantAdminService;
import platform.merchant.service.domain.merchant.ApplicationFormStatus;
import platform.merchant.service.domain.merchant.model.Merchant;
import platform.merchant.service.domain.merchant.model.MerchantApplicationForm;
import platform.merchant.service.infrastructure.persistence.utils.HttpUtils;
import platform.merchant.service.interfaces.factory.ApiResultFactory;
import platform.merchant.service.interfaces.model.internal.merchant.InternalFetchMerchantsByIdsRequest;
import platform.merchant.service.interfaces.model.internal.merchant.InternalMerchantResponses;
import platform.merchant.service.interfaces.model.internal.merchant.InternalUpdateMerchantRequest;
import vn.com.go.routex.identity.security.log.SystemLog;

import java.util.List;

import static platform.core.common.service.persistence.constant.ApiConstant.API_PATH;
import static platform.core.common.service.persistence.constant.ApiConstant.API_VERSION;
import static platform.core.common.service.persistence.constant.ApiConstant.APPLICATIONS_PATH;
import static platform.core.common.service.persistence.constant.ApiConstant.DETAIL_PATH;
import static platform.core.common.service.persistence.constant.ApiConstant.FETCH_PATH;
import static platform.core.common.service.persistence.constant.ApiConstant.INTERNAL_PATH;
import static platform.core.common.service.persistence.constant.ApiConstant.MERCHANT_SERVICE;
import static platform.core.common.service.persistence.constant.ApiConstant.UPDATE_PATH;

@RestController
@RequiredArgsConstructor
@RequestMapping(API_PATH + API_VERSION + MERCHANT_SERVICE + INTERNAL_PATH)
public class InternalMerchantAdminController {

    private final InternalMerchantAdminService internalMerchantAdminService;
    private final ApiResultFactory apiResultFactory;
    private final SystemLog sLog = SystemLog.getLogger(this.getClass());

    @GetMapping(DETAIL_PATH)
    public ResponseEntity<BaseResponse<InternalMerchantResponses.MerchantData>> fetchMerchantDetail(
            HttpServletRequest servletRequest,
            @RequestParam String merchantId
    ) {
        BaseRequest baseRequest = ApiRequestUtils.getBaseRequestOrDefault(servletRequest);
        Merchant merchant = internalMerchantAdminService.fetchMerchantDetail(merchantId, HttpUtils.toContext(baseRequest));

        BaseResponse<InternalMerchantResponses.MerchantData> response = BaseResponse.<InternalMerchantResponses.MerchantData>builder()
                .requestId(baseRequest.getRequestId())
                .requestDateTime(baseRequest.getRequestDateTime())
                .channel(baseRequest.getChannel())
                .result(apiResultFactory.buildSuccess())
                .data(toMerchantData(merchant))
                .build();
        return HttpUtils.buildResponse(baseRequest, response);
    }

    @GetMapping(FETCH_PATH)
    public ResponseEntity<BaseResponse<InternalMerchantResponses.MerchantPage>> fetchMerchants(
            HttpServletRequest servletRequest,
            @RequestParam(required = false) String merchantName,
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        BaseRequest baseRequest = ApiRequestUtils.getBaseRequestOrDefault(servletRequest);
        PagedResult<Merchant> page = internalMerchantAdminService.fetchMerchants(HttpUtils.toContext(baseRequest), merchantName, pageNumber, pageSize);

        List<InternalMerchantResponses.MerchantData> items = page.getItems().stream()
                .map(this::toMerchantData)
                .toList();

        BaseResponse<InternalMerchantResponses.MerchantPage> response = BaseResponse.<InternalMerchantResponses.MerchantPage>builder()
                .requestId(baseRequest.getRequestId())
                .requestDateTime(baseRequest.getRequestDateTime())
                .channel(baseRequest.getChannel())
                .result(apiResultFactory.buildSuccess())
                .data(InternalMerchantResponses.MerchantPage.builder()
                        .items(items)
                        .pagination(InternalMerchantResponses.Pagination.builder()
                                .pageNumber(page.getPageNumber() + 1)
                                .pageSize(page.getPageSize())
                                .totalElements(page.getTotalElements())
                                .totalPages(page.getTotalPages())
                                .build())
                        .build())
                .build();
        return HttpUtils.buildResponse(baseRequest, response);
    }

    @PostMapping("/fetch-by-ids")
    public ResponseEntity<BaseResponse<List<InternalMerchantResponses.MerchantData>>> fetchMerchantsByIds(
            HttpServletRequest servletRequest,
            @RequestBody InternalFetchMerchantsByIdsRequest request
    ) {


        sLog.info("Request: {}", request);
        BaseRequest baseRequest = ApiRequestUtils.getBaseRequestOrDefault(servletRequest);
        List<Merchant> merchants = internalMerchantAdminService.fetchMerchantsByIds(request.getMerchantIds(), HttpUtils.toContext(baseRequest));

        BaseResponse<List<InternalMerchantResponses.MerchantData>> response =
                BaseResponse.<List<InternalMerchantResponses.MerchantData>>builder()
                        .result(apiResultFactory.buildSuccess())
                        .data(merchants.stream().map(this::toMerchantData).toList())
                        .build();
        return HttpUtils.buildResponse(baseRequest, response);
    }

    @GetMapping("/search-ids")
    public ResponseEntity<BaseResponse<List<String>>> searchMerchantIds(
            HttpServletRequest servletRequest,
            @RequestParam String merchantName
    ) {
        BaseRequest baseRequest = ApiRequestUtils.getBaseRequestOrDefault(servletRequest);
        List<String> merchantIds = internalMerchantAdminService.findMerchantIdsByName(merchantName, HttpUtils.toContext(baseRequest));

        BaseResponse<List<String>> response = BaseResponse.<List<String>>builder()
                .result(apiResultFactory.buildSuccess())
                .data(merchantIds)
                .build();
        return HttpUtils.buildResponse(baseRequest, response);
    }

    @PostMapping(UPDATE_PATH)
    public ResponseEntity<BaseResponse<InternalMerchantResponses.MerchantData>> updateMerchant(
            HttpServletRequest servletRequest,
            @RequestBody InternalUpdateMerchantRequest request
    ) {
        BaseRequest baseRequest = ApiRequestUtils.getBaseRequestOrDefault(servletRequest);
        Merchant merchant = internalMerchantAdminService.updateMerchant(request, HttpUtils.toContext(baseRequest));

        BaseResponse<InternalMerchantResponses.MerchantData> response = BaseResponse.<InternalMerchantResponses.MerchantData>builder()
                .result(apiResultFactory.buildSuccess())
                .data(toMerchantData(merchant))
                .build();
        return HttpUtils.buildResponse(baseRequest, response);
    }

    @GetMapping(APPLICATIONS_PATH + FETCH_PATH)
    public ResponseEntity<BaseResponse<InternalMerchantResponses.MerchantApplicationFormPage>> fetchApplicationForms(
            HttpServletRequest servletRequest,
            @RequestParam(required = false) ApplicationFormStatus status,
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        BaseRequest baseRequest = ApiRequestUtils.getBaseRequestOrDefault(servletRequest);
        PagedResult<MerchantApplicationForm> page = internalMerchantAdminService.fetchApplicationForms(
                HttpUtils.toContext(baseRequest), status, pageNumber, pageSize);

        List<InternalMerchantResponses.MerchantApplicationFormData> items = page.getItems().stream()
                .map(this::toApplicationFormData)
                .toList();

        BaseResponse<InternalMerchantResponses.MerchantApplicationFormPage> response =
                BaseResponse.<InternalMerchantResponses.MerchantApplicationFormPage>builder()
                        .result(apiResultFactory.buildSuccess())
                        .data(InternalMerchantResponses.MerchantApplicationFormPage.builder()
                                .items(items)
                                .pagination(InternalMerchantResponses.Pagination.builder()
                                        .pageNumber(page.getPageNumber() + 1)
                                        .pageSize(page.getPageSize())
                                        .totalElements(page.getTotalElements())
                                        .totalPages(page.getTotalPages())
                                        .build())
                                .build())
                        .build();
        return HttpUtils.buildResponse(baseRequest, response);
    }

    @GetMapping(APPLICATIONS_PATH + DETAIL_PATH)
    public ResponseEntity<BaseResponse<InternalMerchantResponses.MerchantApplicationFormData>> fetchApplicationFormDetail(
            HttpServletRequest servletRequest,
            @RequestParam String applicationFormId
    ) {
        BaseRequest baseRequest = ApiRequestUtils.getBaseRequestOrDefault(servletRequest);
        MerchantApplicationForm form = internalMerchantAdminService.fetchApplicationFormDetail(applicationFormId, HttpUtils.toContext(baseRequest));

        BaseResponse<InternalMerchantResponses.MerchantApplicationFormData> response =
                BaseResponse.<InternalMerchantResponses.MerchantApplicationFormData>builder()
                        .result(apiResultFactory.buildSuccess())
                        .data(toApplicationFormData(form))
                        .build();
        return HttpUtils.buildResponse(baseRequest, response);
    }

    private InternalMerchantResponses.MerchantData toMerchantData(Merchant merchant) {
        return InternalMerchantResponses.MerchantData.builder()
                .id(merchant.getId())
                .code(merchant.getCode())
                .slug(merchant.getSlug())
                .displayName(merchant.getDisplayName())
                .legalName(merchant.getLegalName())
                .taxCode(merchant.getTaxCode())
                .businessLicenseNumber(merchant.getBusinessLicenseNumber())
                .businessLicenseUrl(merchant.getBusinessLicenseUrl())
                .phone(merchant.getPhone())
                .email(merchant.getEmail())
                .logoUrl(merchant.getLogoUrl())
                .description(merchant.getDescription())
                .address(merchant.getAddress())
                .ward(merchant.getWard())
                .province(merchant.getProvince())
                .country(merchant.getCountry())
                .postalCode(merchant.getPostalCode())
                .representativeName(merchant.getRepresentativeName())
                .contactName(merchant.getContactName())
                .contactPhone(merchant.getContactPhone())
                .contactEmail(merchant.getContactEmail())
                .ownerFullName(merchant.getOwnerFullName())
                .ownerPhone(merchant.getOwnerPhone())
                .ownerEmail(merchant.getOwnerEmail())
                .bankAccountName(merchant.getBankAccountName())
                .bankAccountNumber(merchant.getBankAccountNumber())
                .bankName(merchant.getBankName())
                .bankBranch(merchant.getBankBranch())
                .commissionRate(merchant.getCommissionRate())
                .status(merchant.getStatus())
                .approvedAt(merchant.getApprovedAt())
                .approvedBy(merchant.getApprovedBy())
                .build();
    }

    private InternalMerchantResponses.MerchantApplicationFormData toApplicationFormData(MerchantApplicationForm form) {
        return InternalMerchantResponses.MerchantApplicationFormData.builder()
                .id(form.getId())
                .formCode(form.getFormCode())
                .displayName(form.getDisplayName())
                .legalName(form.getLegalName())
                .taxCode(form.getTaxCode())
                .businessLicense(form.getBusinessLicense())
                .businessLicenseUrl(form.getBusinessLicenseUrl())
                .logoUrl(form.getLogoUrl())
                .description(form.getDescription())
                .slug(form.getSlug())
                .submittedBy(form.getSubmittedBy())
                .submittedAt(form.getSubmittedAt())
                .approvedBy(form.getApprovedBy())
                .approvedAt(form.getApprovedAt())
                .rejectedBy(form.getRejectedBy())
                .rejectionReason(form.getRejectionReason())
                .country(form.getCountry())
                .province(form.getProvince())
                .ward(form.getWard())
                .address(form.getAddress())
                .postalCode(form.getPostalCode())
                .contactName(form.getContact() == null ? null : form.getContact().getContactName())
                .contactPhone(form.getContact() == null ? null : form.getContact().getContactPhone())
                .contactEmail(form.getContact() == null ? null : form.getContact().getContactEmail())
                .ownerName(form.getOwnerInfo() == null ? null : form.getOwnerInfo().getOwnerName())
                .ownerFullName(form.getOwnerInfo() == null ? null : form.getOwnerInfo().getOwnerFullName())
                .ownerPhone(form.getOwnerInfo() == null ? null : form.getOwnerInfo().getOwnerPhone())
                .ownerEmail(form.getOwnerInfo() == null ? null : form.getOwnerInfo().getOwnerEmail())
                .bankAccountName(form.getBankInfo() == null ? null : form.getBankInfo().getBankAccountName())
                .bankAccountNumber(form.getBankInfo() == null ? null : form.getBankInfo().getBankAccountNumber())
                .bankName(form.getBankInfo() == null ? null : form.getBankInfo().getBankName())
                .bankBranch(form.getBankInfo() == null ? null : form.getBankInfo().getBankBranch())
                .status(form.getStatus())
                .build();
    }
}
