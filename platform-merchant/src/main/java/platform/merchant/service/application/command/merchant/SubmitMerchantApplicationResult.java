package platform.merchant.service.application.command.merchant;

import lombok.Builder;

import java.time.OffsetDateTime;

@Builder
public record SubmitMerchantApplicationResult(
        String applicationId,
        String formCode,
        String displayName,
        String legalName,
        String status,
        OffsetDateTime submittedAt
) {
}
