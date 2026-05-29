package platform.merchant.service.application.command.merchant;

import lombok.Builder;
import platform.core.common.service.common.RequestContext;

@Builder
public record SubmitMerchantApplicationCommand(
        RequestContext context,
        String displayName,
        String legalName,
        String taxCode,
        String businessLicense,
        String businessLicenseUrl,
        String logoUrl,
        String description,
        String slug,
        Address address,
        Contact contact,
        BankInfo bankInfo,
        OwnerInfo ownerInfo
) {

    @Builder
    public record Address(
            String country,
            String province,
            String address,
            String city,
            String ward,
            String postalCode
    ) {

    }
    @Builder
    public record Contact(
            String contactName,
            String contactPhone,
            String contactEmail
    ) {
    }

    @Builder
    public record BankInfo(
            String bankName,
            String bankBranch,
            String bankAccountName,
            String bankAccountNumber
    ) {
    }

    @Builder
    public record OwnerInfo(
            String ownerName,
            String ownerFullName,
            String ownerPhone,
            String ownerEmail
    ) {
    }
}
