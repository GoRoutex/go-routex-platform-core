package platform.merchant.service.application.command.merchant;

import lombok.Builder;

import java.time.OffsetDateTime;

@Builder
public record AcceptMerchantApplicationResult(
        String applicationId,
        String formCode,
        String merchantId,
        String merchantCode,
        String merchantName,
        String status,
        String approvedBy,
        OffsetDateTime approvedAt
) {
}
