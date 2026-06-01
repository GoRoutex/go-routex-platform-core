package platform.management.service.interfaces.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import platform.management.service.application.command.merchant.FetchMerchantDetailQuery;
import platform.management.service.application.command.merchant.FetchMerchantDetailResult;
import platform.management.service.application.command.merchant.FetchMerchantsQuery;
import platform.management.service.application.command.merchant.FetchMerchantsResult;
import platform.management.service.application.command.merchant.UpdateMerchantCommand;
import platform.management.service.application.command.merchant.UpdateMerchantResult;
import platform.management.service.application.services.MerchantManagementService;
import platform.management.service.infrastructure.persistence.utils.ApiRequestUtils;
import platform.management.service.infrastructure.persistence.utils.HttpUtils;
import platform.management.service.interfaces.factory.ApiResultFactory;
import platform.management.service.interfaces.models.merchant.FetchMerchantDetailResponse;
import platform.management.service.interfaces.models.merchant.FetchMerchantsResponse;
import platform.management.service.interfaces.models.merchant.UpdateMerchantRequest;
import platform.management.service.interfaces.models.merchant.UpdateMerchantResponse;
import platform.core.common.service.api.BaseRequest;

import java.util.stream.Collectors;

import static platform.core.common.service.persistence.constant.ApiConstant.API_PATH;
import static platform.core.common.service.persistence.constant.ApiConstant.API_VERSION;
import static platform.core.common.service.persistence.constant.ApiConstant.DETAIL_PATH;
import static platform.core.common.service.persistence.constant.ApiConstant.FETCH_PATH;
import static platform.core.common.service.persistence.constant.ApiConstant.MANAGEMENT_PATH;
import static platform.core.common.service.persistence.constant.ApiConstant.MERCHANT_SERVICE;
import static platform.core.common.service.persistence.constant.ApiConstant.UPDATE_PATH;


@RestController
@RequiredArgsConstructor
@RequestMapping(API_PATH + API_VERSION + MANAGEMENT_PATH)
public class MerchantManagementController {

    private final MerchantManagementService merchantManagementService;
    private final ApiResultFactory apiResultFactory;

    @GetMapping(MERCHANT_SERVICE + DETAIL_PATH)
    public ResponseEntity<FetchMerchantDetailResponse> fetchMerchantDetail(
            HttpServletRequest servletRequest,
            @RequestParam String merchantId
    ) {
        BaseRequest baseRequest = ApiRequestUtils.getBaseRequestOrDefault(servletRequest);

        FetchMerchantDetailResult result = merchantManagementService.fetchMerchantDetail(
                FetchMerchantDetailQuery.builder()
                        .context(HttpUtils.toContext(baseRequest))
                        .merchantId(merchantId)
                        .build()
        );

        FetchMerchantDetailResponse response = FetchMerchantDetailResponse.builder()
                .result(apiResultFactory.buildSuccess())
                .data(FetchMerchantDetailResponse.FetchMerchantDetailData.builder()
                        .id(result.id())
                        .code(result.code())
                        .slug(result.slug())
                        .displayName(result.displayName())
                        .legalName(result.legalName())
                        .taxCode(result.taxCode())
                        .businessLicenseNumber(result.businessLicenseNumber())
                        .businessLicenseUrl(result.businessLicenseUrl())
                        .phone(result.phone())
                        .email(result.email())
                        .logoUrl(result.logoUrl())
                        .description(result.description())
                        .address(result.address())
                        .ward(result.ward())
                        .province(result.province())
                        .country(result.country())
                        .postalCode(result.postalCode())
                        .representativeName(result.representativeName())
                        .contactName(result.contactName())
                        .contactPhone(result.contactPhone())
                        .contactEmail(result.contactEmail())
                        .ownerFullName(result.ownerFullName())
                        .ownerPhone(result.ownerPhone())
                        .ownerEmail(result.ownerEmail())
                        .bankAccountName(result.bankAccountName())
                        .bankAccountNumber(result.bankAccountNumber())
                        .bankName(result.bankName())
                        .bankBranch(result.bankBranch())
                        .commissionRate(result.commissionRate())
                        .status(result.status())
                        .approvedAt(result.approvedAt())
                        .approvedBy(result.approvedBy())
                        .build())
                .build();

        return HttpUtils.buildResponse(baseRequest, response);
    }

    @GetMapping(MERCHANT_SERVICE + FETCH_PATH)
    public ResponseEntity<FetchMerchantsResponse> fetchMerchants(
            HttpServletRequest servletRequest,
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        BaseRequest baseRequest = ApiRequestUtils.getBaseRequestOrDefault(servletRequest);

        FetchMerchantsResult result = merchantManagementService.fetchMerchants(FetchMerchantsQuery.builder()
                .context(HttpUtils.toContext(baseRequest))
                .pageNumber(String.valueOf(pageNumber))
                .pageSize(String.valueOf(pageSize))
                .build());

        FetchMerchantsResponse response = FetchMerchantsResponse.builder()
                .result(apiResultFactory.buildSuccess())
                .data(FetchMerchantsResponse.FetchMerchantsResponsePage.builder()
                        .items(result.items().stream()
                                .map(item -> FetchMerchantsResponse.FetchMerchantResponseData.builder()
                                        .id(item.id())
                                        .code(item.code())
                                        .slug(item.slug())
                                        .displayName(item.displayName())
                                        .legalName(item.legalName())
                                        .taxCode(item.taxCode())
                                        .businessLicenseNumber(item.businessLicenseNumber())
                                        .businessLicenseUrl(item.businessLicenseUrl())
                                        .phone(item.phone())
                                        .email(item.email())
                                        .logoUrl(item.logoUrl())
                                        .description(item.description())
                                        .address(item.address())
                                        .ward(item.ward())
                                        .province(item.province())
                                        .country(item.country())
                                        .postalCode(item.postalCode())
                                        .representativeName(item.representativeName())
                                        .contactName(item.contactName())
                                        .contactPhone(item.contactPhone())
                                        .contactEmail(item.contactEmail())
                                        .ownerFullName(item.ownerFullName())
                                        .ownerPhone(item.ownerPhone())
                                        .ownerEmail(item.ownerEmail())
                                        .bankAccountName(item.bankAccountName())
                                        .bankAccountNumber(item.bankAccountNumber())
                                        .bankName(item.bankName())
                                        .bankBranch(item.bankBranch())
                                        .commissionRate(item.commissionRate())
                                        .status(item.status())
                                        .approvedAt(item.approvedAt())
                                        .approvedBy(item.approvedBy())
                                        .build())
                                .collect(Collectors.toList()))
                        .totalPartners(result.totalPartners())
                        .totalRevenueShare(result.totalRevenueShare())
                        .avgRating(result.avgRating())
                        .numberOfPendingApps(result.numberOfPendingApps())
                        .pagination(FetchMerchantsResponse.Pagination.builder()
                                .pageNumber(result.pageNumber())
                                .pageSize(result.pageSize())
                                .totalElements(result.totalElements())
                                .totalPages(result.totalPages())
                                .build())
                        .build())
                .build();

        return HttpUtils.buildResponse(baseRequest, response);
    }

    @PostMapping(MERCHANT_SERVICE + UPDATE_PATH)
    public ResponseEntity<UpdateMerchantResponse> updateMerchant(
            @Valid @RequestBody UpdateMerchantRequest request
    ) {
        UpdateMerchantResult result = merchantManagementService.updateMerchant(UpdateMerchantCommand.builder()
                .context(HttpUtils.toContext(request))

                .merchantId(request.getData().getMerchantId())
                .code(request.getData().getCode())
                .slug(request.getData().getSlug())
                .displayName(request.getData().getDisplayName())
                .legalName(request.getData().getLegalName())
                .taxCode(request.getData().getTaxCode())
                .businessLicenseNumber(request.getData().getBusinessLicenseNumber())
                .businessLicenseUrl(request.getData().getBusinessLicenseUrl())
                .phone(request.getData().getPhone())
                .email(request.getData().getEmail())
                .logoUrl(request.getData().getLogoUrl())
                .description(request.getData().getDescription())
                .address(request.getData().getAddress())
                .ward(request.getData().getWard())
                .province(request.getData().getProvince())
                .country(request.getData().getCountry())
                .postalCode(request.getData().getPostalCode())
                .representativeName(request.getData().getRepresentativeName())
                .contactName(request.getData().getContactName())
                .contactPhone(request.getData().getContactPhone())
                .contactEmail(request.getData().getContactEmail())
                .ownerFullName(request.getData().getOwnerFullName())
                .ownerPhone(request.getData().getOwnerPhone())
                .ownerEmail(request.getData().getOwnerEmail())
                .bankAccountName(request.getData().getBankAccountName())
                .bankAccountNumber(request.getData().getBankAccountNumber())
                .bankName(request.getData().getBankName())
                .bankBranch(request.getData().getBankBranch())
                .commissionRate(request.getData().getCommissionRate())
                .status(request.getData().getStatus())
                .updatedBy(request.getData().getUpdatedBy())
                .build());

        UpdateMerchantResponse response = UpdateMerchantResponse.builder()
                .result(apiResultFactory.buildSuccess())
                .data(UpdateMerchantResponse.UpdateMerchantResponseData.builder()
                        .id(result.id())
                        .code(result.code())
                        .slug(result.slug())
                        .displayName(result.displayName())
                        .legalName(result.legalName())
                        .taxCode(result.taxCode())
                        .businessLicenseNumber(result.businessLicenseNumber())
                        .businessLicenseUrl(result.businessLicenseUrl())
                        .phone(result.phone())
                        .email(result.email())
                        .logoUrl(result.logoUrl())
                        .description(result.description())
                        .address(result.address())
                        .ward(result.ward())
                        .province(result.province())
                        .country(result.country())
                        .postalCode(result.postalCode())
                        .representativeName(result.representativeName())
                        .contactName(result.contactName())
                        .contactPhone(result.contactPhone())
                        .contactEmail(result.contactEmail())
                        .ownerFullName(result.ownerFullName())
                        .ownerPhone(result.ownerPhone())
                        .ownerEmail(result.ownerEmail())
                        .bankAccountName(result.bankAccountName())
                        .bankAccountNumber(result.bankAccountNumber())
                        .bankName(result.bankName())
                        .bankBranch(result.bankBranch())
                        .commissionRate(result.commissionRate())
                        .status(result.status())
                        .updatedBy(result.updatedBy())
                        .build())
                .build();

        return HttpUtils.buildResponse(request, response);
    }
}
