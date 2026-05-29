package platform.management.service.application.command.merchant;

import lombok.Builder;
import platform.management.service.domain.merchant.MerchantStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Builder
public record FetchMerchantsResult(
        List<FetchMerchantItemResult> items,
        long totalPartners,
        BigDecimal totalRevenueShare,
        BigDecimal avgRating,
        long numberOfPendingApps,
        int pageNumber,
        int pageSize,
        long totalElements,
        int totalPages
) {

    @Builder
    public record FetchMerchantItemResult(
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
}
