package platform.management.service.application.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import platform.core.common.service.persistence.exception.BusinessException;
import platform.core.common.service.persistence.utils.ExceptionUtils;
import platform.management.service.application.command.merchant.FetchMerchantDetailQuery;
import platform.management.service.application.command.merchant.FetchMerchantDetailResult;
import platform.management.service.application.command.merchant.FetchMerchantsQuery;
import platform.management.service.application.command.merchant.FetchMerchantsResult;
import platform.management.service.application.command.merchant.UpdateMerchantCommand;
import platform.management.service.application.command.merchant.UpdateMerchantResult;
import platform.management.service.application.services.MerchantManagementService;
import platform.management.service.infrastructure.integration.common.support.InternalApiExecutor;
import platform.management.service.infrastructure.integration.merchantplatform.client.MerchantPlatformInternalClient;
import platform.management.service.infrastructure.integration.merchantplatform.model.MerchantPlatformInternalModels;
import platform.management.service.infrastructure.integration.merchantplatform.model.MerchantPlatformUpdateMerchantRequest;
import platform.management.service.infrastructure.persistence.utils.ApiRequestUtils;
import platform.merchant.service.domain.merchant.ApplicationFormStatus;

import java.math.BigDecimal;
import java.util.List;

import static platform.core.common.service.persistence.constant.ErrorConstant.INVALID_COMMISSION_RATE;
import static platform.core.common.service.persistence.constant.ErrorConstant.INVALID_INPUT_ERROR;
import static platform.core.common.service.persistence.constant.ErrorConstant.INVALID_PAGE_NUMBER;
import static platform.core.common.service.persistence.constant.ErrorConstant.INVALID_PAGE_SIZE;


@Service
@RequiredArgsConstructor
public class MerchantManagementServiceImpl implements MerchantManagementService {

    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final int DEFAULT_PAGE_NUMBER = 1;

    private final MerchantPlatformInternalClient merchantPlatformInternalClient;

    @Override
    public FetchMerchantDetailResult fetchMerchantDetail(FetchMerchantDetailQuery query) {
        MerchantPlatformInternalModels.MerchantData merchant = InternalApiExecutor.execute(
                query.context(),
                () -> merchantPlatformInternalClient.fetchMerchantDetail(query.merchantId())
        );

        return FetchMerchantDetailResult.builder()
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

    @Override
    public FetchMerchantsResult fetchMerchants(FetchMerchantsQuery query) {
        int pageSize = ApiRequestUtils.parseIntOrDefault(query.pageSize(), DEFAULT_PAGE_SIZE, "pageSize",
                query.context().requestId(), query.context().requestDateTime(), query.context().channel());
        int pageNumber = ApiRequestUtils.parseIntOrDefault(query.pageNumber(), DEFAULT_PAGE_NUMBER, "pageNumber",
                query.context().requestId(), query.context().requestDateTime(), query.context().channel());

        if (pageSize < 1 || pageSize > 100) {
            throw new BusinessException(query.context().requestId(), query.context().requestDateTime(), query.context().channel(),
                    ExceptionUtils.buildResultResponse(INVALID_INPUT_ERROR, INVALID_PAGE_SIZE));
        }
        if (pageNumber < 1) {
            throw new BusinessException(query.context().requestId(), query.context().requestDateTime(), query.context().channel(),
                    ExceptionUtils.buildResultResponse(INVALID_INPUT_ERROR, INVALID_PAGE_NUMBER));
        }

        MerchantPlatformInternalModels.MerchantPage page = InternalApiExecutor.execute(
                query.context(),
                () -> merchantPlatformInternalClient.fetchMerchants(pageNumber, pageSize)
        );

        List<FetchMerchantsResult.FetchMerchantItemResult> items = page.getItems().stream()
                .map(merchant -> FetchMerchantsResult.FetchMerchantItemResult.builder()
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
                        .build())
                .toList();

        long pendingApps = InternalApiExecutor.execute(
                query.context(),
                () -> merchantPlatformInternalClient.fetchApplicationForms(ApplicationFormStatus.SUBMITTED, 1, 1)
        ).getPagination().getTotalElements();

        return FetchMerchantsResult.builder()
                .items(items)
                .totalPartners(page.getPagination().getTotalElements())
                .totalRevenueShare(BigDecimal.ZERO)
                .avgRating(BigDecimal.ZERO)
                .numberOfPendingApps(pendingApps)
                .pageNumber(page.getPagination().getPageNumber())
                .pageSize(page.getPagination().getPageSize())
                .totalElements(page.getPagination().getTotalElements())
                .totalPages(page.getPagination().getTotalPages())
                .build();
    }

    @Override
    public UpdateMerchantResult updateMerchant(UpdateMerchantCommand command) {
        validateCommissionRate(command.commissionRate(), command);

        MerchantPlatformUpdateMerchantRequest request = new MerchantPlatformUpdateMerchantRequest();
        request.setMerchantId(command.merchantId());
        request.setCode(command.code());
        request.setSlug(command.slug());
        request.setDisplayName(command.displayName());
        request.setLegalName(command.legalName());
        request.setTaxCode(command.taxCode());
        request.setBusinessLicenseNumber(command.businessLicenseNumber());
        request.setBusinessLicenseUrl(command.businessLicenseUrl());
        request.setPhone(command.phone());
        request.setEmail(command.email());
        request.setLogoUrl(command.logoUrl());
        request.setDescription(command.description());
        request.setAddress(command.address());
        request.setWard(command.ward());
        request.setProvince(command.province());
        request.setCountry(command.country());
        request.setPostalCode(command.postalCode());
        request.setRepresentativeName(command.representativeName());
        request.setContactName(command.contactName());
        request.setContactPhone(command.contactPhone());
        request.setContactEmail(command.contactEmail());
        request.setOwnerFullName(command.ownerFullName());
        request.setOwnerPhone(command.ownerPhone());
        request.setOwnerEmail(command.ownerEmail());
        request.setBankAccountName(command.bankAccountName());
        request.setBankAccountNumber(command.bankAccountNumber());
        request.setBankName(command.bankName());
        request.setBankBranch(command.bankBranch());
        request.setCommissionRate(command.commissionRate());
        request.setStatus(command.status());
        request.setApprovedAt(command.approvedAt());
        request.setApprovedBy(command.approvedBy());
        request.setUpdatedBy(command.updatedBy());

        MerchantPlatformInternalModels.MerchantData saved = InternalApiExecutor.execute(
                command.context(),
                () -> merchantPlatformInternalClient.updateMerchant(request)
        );

        return UpdateMerchantResult.builder()
                .id(saved.getId())
                .code(saved.getCode())
                .slug(saved.getSlug())
                .displayName(saved.getDisplayName())
                .legalName(saved.getLegalName())
                .taxCode(saved.getTaxCode())
                .businessLicenseNumber(saved.getBusinessLicenseNumber())
                .businessLicenseUrl(saved.getBusinessLicenseUrl())
                .phone(saved.getPhone())
                .email(saved.getEmail())
                .logoUrl(saved.getLogoUrl())
                .description(saved.getDescription())
                .address(saved.getAddress())
                .ward(saved.getWard())
                .province(saved.getProvince())
                .country(saved.getCountry())
                .postalCode(saved.getPostalCode())
                .representativeName(saved.getRepresentativeName())
                .contactName(saved.getContactName())
                .contactPhone(saved.getContactPhone())
                .contactEmail(saved.getContactEmail())
                .ownerFullName(saved.getOwnerFullName())
                .ownerPhone(saved.getOwnerPhone())
                .ownerEmail(saved.getOwnerEmail())
                .bankAccountName(saved.getBankAccountName())
                .bankAccountNumber(saved.getBankAccountNumber())
                .bankName(saved.getBankName())
                .bankBranch(saved.getBankBranch())
                .commissionRate(saved.getCommissionRate())
                .status(saved.getStatus())
                .updatedBy(command.updatedBy())
                .build();
    }

    private void validateCommissionRate(BigDecimal commissionRate, UpdateMerchantCommand command) {
        if (commissionRate == null) {
            return;
        }

        if (commissionRate.compareTo(BigDecimal.ZERO) < 0 || commissionRate.compareTo(new BigDecimal("100")) > 0) {
            throw new BusinessException(
                    command.context().requestId(),
                    command.context().requestDateTime(),
                    command.context().channel(),
                    ExceptionUtils.buildResultResponse(INVALID_INPUT_ERROR, INVALID_COMMISSION_RATE)
            );
        }
    }
}
