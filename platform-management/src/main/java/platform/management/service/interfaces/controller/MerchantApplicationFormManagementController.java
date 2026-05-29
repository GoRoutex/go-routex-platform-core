package platform.management.service.interfaces.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import platform.management.service.application.command.merchant.FetchMerchantApplicationFormDetailQuery;
import platform.management.service.application.command.merchant.FetchMerchantApplicationFormDetailResult;
import platform.management.service.application.command.merchant.FetchPendingMerchantApplicationFormsQuery;
import platform.management.service.application.command.merchant.FetchPendingMerchantApplicationFormsResult;
import platform.management.service.application.services.MerchantApplicationFormManagementService;
import platform.management.service.infrastructure.persistence.utils.ApiRequestUtils;
import platform.management.service.infrastructure.persistence.utils.HttpUtils;
import platform.management.service.interfaces.factory.ApiResultFactory;
import platform.management.service.interfaces.models.merchant.FetchMerchantApplicationFormDetailResponse;
import platform.management.service.interfaces.models.merchant.FetchPendingMerchantApplicationFormsResponse;
import platform.core.common.service.api.BaseRequest;

import java.util.stream.Collectors;

import static vn.com.routex.platform.common.constant.ApiConstant.API_PATH;
import static vn.com.routex.platform.common.constant.ApiConstant.API_VERSION;
import static vn.com.routex.platform.common.constant.ApiConstant.APPLICATION_FORM;
import static vn.com.routex.platform.common.constant.ApiConstant.DETAIL_PATH;
import static vn.com.routex.platform.common.constant.ApiConstant.FETCH_PATH;
import static vn.com.routex.platform.common.constant.ApiConstant.MANAGEMENT_PATH;


@RestController
@RequiredArgsConstructor
@RequestMapping(API_PATH + API_VERSION + MANAGEMENT_PATH)
//@PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
public class MerchantApplicationFormManagementController {

    private final MerchantApplicationFormManagementService merchantApplicationFormManagementService;
    private final ApiResultFactory apiResultFactory;

    @GetMapping(APPLICATION_FORM + FETCH_PATH)
    public ResponseEntity<FetchPendingMerchantApplicationFormsResponse> fetchPendingApplicationForms(
            HttpServletRequest servletRequest,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        BaseRequest baseRequest = ApiRequestUtils.getBaseRequestOrDefault(servletRequest);

        FetchPendingMerchantApplicationFormsResult result =
                merchantApplicationFormManagementService.fetchPendingApplicationForms(
                        FetchPendingMerchantApplicationFormsQuery.builder()
                                .context(HttpUtils.toContext(baseRequest))
                                .status(status)
                                .pageNumber(String.valueOf(pageNumber))
                                .pageSize(String.valueOf(pageSize))
                                .build()
                );

        FetchPendingMerchantApplicationFormsResponse response = FetchPendingMerchantApplicationFormsResponse.builder()
                .result(apiResultFactory.buildSuccess())
                .data(FetchPendingMerchantApplicationFormsResponse.FetchPendingMerchantApplicationFormsPage.builder()
                        .items(result.items().stream()
                                .map(item -> FetchPendingMerchantApplicationFormsResponse.PendingMerchantApplicationFormData.builder()
                                        .id(item.id())
                                        .formCode(item.formCode())
                                        .displayName(item.displayName())
                                        .legalName(item.legalName())
                                        .taxCode(item.taxCode())
                                        .businessLicense(item.businessLicense())
                                        .businessLicenseUrl(item.businessLicenseUrl())
                                        .country(item.country())
                                        .province(item.province())
                                        .address(item.address())
                                        .logoUrl(item.logoUrl())
                                        .ward(item.ward())
                                        .postalCode(item.postalCode())
                                        .description(item.description())
                                        .slug(item.slug())
                                        .submittedBy(item.submittedBy())
                                        .submittedAt(item.submittedAt())
                                        .status(item.status())
                                        .contactName(item.contactName())
                                        .contactPhone(item.contactPhone())
                                        .contactEmail(item.contactEmail())
                                        .build())
                                .collect(Collectors.toList()))
                        .pagination(FetchPendingMerchantApplicationFormsResponse.Pagination.builder()
                                .pageNumber(result.pageNumber())
                                .pageSize(result.pageSize())
                                .totalElements(result.totalElements())
                                .totalPages(result.totalPages())
                                .build())
                        .build())
                .build();

        return HttpUtils.buildResponse(baseRequest, response);
    }

    @GetMapping(APPLICATION_FORM + DETAIL_PATH)
    public ResponseEntity<FetchMerchantApplicationFormDetailResponse> fetchApplicationFormDetail(
            HttpServletRequest servletRequest,
            @RequestParam String applicationFormId
    ) {
        BaseRequest baseRequest = ApiRequestUtils.getBaseRequestOrDefault(servletRequest);

        FetchMerchantApplicationFormDetailResult result =
                merchantApplicationFormManagementService.fetchApplicationFormDetail(
                        FetchMerchantApplicationFormDetailQuery.builder()
                                .context(HttpUtils.toContext(baseRequest))
                                .applicationFormId(applicationFormId)
                                .build()
                );

        FetchMerchantApplicationFormDetailResponse response = FetchMerchantApplicationFormDetailResponse.builder()
                .result(apiResultFactory.buildSuccess())
                .data(FetchMerchantApplicationFormDetailResponse.FetchMerchantApplicationFormDetailData.builder()
                        .id(result.id())
                        .formCode(result.formCode())
                        .displayName(result.displayName())
                        .legalName(result.legalName())
                        .taxCode(result.taxCode())
                        .businessLicense(result.businessLicense())
                        .businessLicenseUrl(result.businessLicenseUrl())
                        .address(FetchMerchantApplicationFormDetailResponse.AddressData.builder()
                                .province(result.address().province())
                                .country(result.address().country())
                                .address(result.address().address())
                                .ward(result.address().ward())
                                .postalCode(result.address().postalCode())
                                .build())
                        .description(result.description())
                        .slug(result.slug())
                        .approvedBy(result.approvedBy())
                        .approvedAt(result.approvedAt())
                        .rejectedBy(result.rejectedBy())
                        .logoUrl(result.logoUrl())
                        .rejectionReason(result.rejectionReason())
                        .status(result.status())
                        .submittedBy(result.submittedBy())
                        .submittedAt(result.submittedAt())
                        .contact(FetchMerchantApplicationFormDetailResponse.ContactData.builder()
                                .contactEmail(result.contact() == null ? null : result.contact().contactEmail())
                                .contactName(result.contact() == null ? null : result.contact().contactName())
                                .contactPhone(result.contact() == null ? null : result.contact().contactPhone())
                                .build())
                        .bankInfo(FetchMerchantApplicationFormDetailResponse.BankInfoData.builder()
                                .bankAccountName(result.bankInfo() == null ? null : result.bankInfo().bankAccountName())
                                .bankAccountNumber(result.bankInfo() == null ? null : result.bankInfo().bankAccountNumber())
                                .bankBranch(result.bankInfo() == null ? null : result.bankInfo().bankBranch())
                                .bankName(result.bankInfo() == null ? null : result.bankInfo().bankName())
                                .build())
                        .ownerInfo(FetchMerchantApplicationFormDetailResponse.OwnerInfoData.builder()
                                .ownerEmail(result.ownerInfo() == null ? null : result.ownerInfo().ownerEmail())
                                .ownerFullName(result.ownerInfo() == null ? null : result.ownerInfo().ownerFullName())
                                .ownerName(result.ownerInfo() == null ? null : result.ownerInfo().ownerName())
                                .ownerPhone(result.ownerInfo() == null ? null : result.ownerInfo().ownerPhone())
                                .build())
                        .build())
                .build();

        return HttpUtils.buildResponse(baseRequest, response);
    }
}
