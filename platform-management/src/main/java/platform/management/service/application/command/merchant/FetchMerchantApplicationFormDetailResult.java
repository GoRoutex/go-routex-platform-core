package platform.management.service.application.command.merchant;

import lombok.Builder;
import platform.merchant.service.domain.merchant.ApplicationFormStatus;

import java.time.OffsetDateTime;

@Builder
public record FetchMerchantApplicationFormDetailResult(
        String id,
        String formCode,
        String displayName,
        String legalName,
        String taxCode,
        String businessLicense,
        String businessLicenseUrl,
        AddressResult address,
        String logoUrl,
        String description,
        String slug,
        String approvedBy,
        OffsetDateTime approvedAt,
        String rejectedBy,
        String rejectionReason,
        ApplicationFormStatus status,
        String submittedBy,
        OffsetDateTime submittedAt,
        ContactResult contact,
        BankInfoResult bankInfo,
        OwnerInfoResult ownerInfo
) {

    @Builder
    public record AddressResult(
            String province,
            String ward,
            String address,
            String postalCode,
            String country
    ) {

    }
    @Builder
    public record ContactResult(
            String contactEmail,
            String contactName,
            String contactPhone
    ) {
    }

    @Builder
    public record BankInfoResult(
            String bankAccountName,
            String bankAccountNumber,
            String bankBranch,
            String bankName
    ) {
    }

    @Builder
    public record OwnerInfoResult(
            String ownerEmail,
            String ownerFullName,
            String ownerName,
            String ownerPhone
    ) {
    }
}
