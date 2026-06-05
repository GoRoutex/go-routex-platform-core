package platform.merchant.service.application.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import platform.core.common.service.persistence.exception.BusinessException;
import platform.core.common.service.persistence.utils.ExceptionUtils;
import platform.merchant.service.application.command.merchant.GetMyMerchantCommand;
import platform.merchant.service.application.command.merchant.GetMyMerchantResult;
import platform.merchant.service.application.service.MerchantAccessService;
import platform.merchant.service.domain.merchant.model.Merchant;
import platform.merchant.service.domain.merchant.port.MerchantRepositoryPort;

import static platform.core.common.service.persistence.constant.ErrorConstant.MERCHANT_NOT_FOUND_BY_ID;
import static platform.core.common.service.persistence.constant.ErrorConstant.RECORD_NOT_FOUND;


@Service
@RequiredArgsConstructor
public class MerchantAccessServiceImpl implements MerchantAccessService {

    private final MerchantRepositoryPort merchantRepositoryPort;

    @Override
    public GetMyMerchantResult fetchMerchantDetail(GetMyMerchantCommand query) {
        Merchant merchant = merchantRepositoryPort.findById(query.merchantId())
                .orElseThrow(() -> new BusinessException(
                        query.context().requestId(),
                        query.context().requestDateTime(),
                        query.context().channel(),
                        ExceptionUtils.buildResultResponse(
                                RECORD_NOT_FOUND,
                                String.format(MERCHANT_NOT_FOUND_BY_ID, query.merchantId())
                        )
                ));

        return GetMyMerchantResult.builder()
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
}
