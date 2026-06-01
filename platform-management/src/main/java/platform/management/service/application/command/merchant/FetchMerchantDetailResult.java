package platform.management.service.application.command.merchant;

import lombok.Builder;
import platform.merchant.service.domain.merchant.MerchantStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Builder
public record FetchMerchantDetailResult(
        String id,
        String code,
        String slug,
        String displayName,
        String legalName,
        String taxCode,
        String businessLicenseNumber,
        String businessLicenseUrl,
        String phone,
        String email,
        String logoUrl,
        String description,
        String address,
        String ward,
        String province,
        String country,
        String postalCode,
        String representativeName,
        String contactName,
        String contactPhone,
        String contactEmail,
        String ownerFullName,
        String ownerPhone,
        String ownerEmail,
        String bankAccountName,
        String bankAccountNumber,
        String bankName,
        String bankBranch,
        BigDecimal commissionRate,
        MerchantStatus status,
        OffsetDateTime approvedAt,
        String approvedBy
) {
}
