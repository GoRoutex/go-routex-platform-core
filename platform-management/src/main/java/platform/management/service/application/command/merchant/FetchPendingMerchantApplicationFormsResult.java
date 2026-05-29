package platform.management.service.application.command.merchant;

import lombok.Builder;
import platform.merchant.service.domain.merchant.ApplicationFormStatus;

import java.time.OffsetDateTime;
import java.util.List;

@Builder
public record FetchPendingMerchantApplicationFormsResult(
        List<PendingMerchantApplicationFormItemResult> items,
        int pageNumber,
        int pageSize,
        long totalElements,
        int totalPages
) {
    @Builder
    public record PendingMerchantApplicationFormItemResult(
            String id,
            String formCode,
            String displayName,
            String legalName,
            String taxCode,
            String businessLicense,
            String businessLicenseUrl,
            String country,
            String province,
            String address,
            String ward,
            String postalCode,
            String description,
            String slug,
            String submittedBy,
            OffsetDateTime submittedAt,
            ApplicationFormStatus status,
            String contactName,
            String logoUrl,
            String contactPhone,
            String contactEmail
    ) {
    }
}
