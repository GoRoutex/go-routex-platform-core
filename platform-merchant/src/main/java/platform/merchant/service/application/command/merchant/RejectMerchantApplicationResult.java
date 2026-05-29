package platform.merchant.service.application.command.merchant;

import lombok.Builder;

@Builder
public record RejectMerchantApplicationResult(
        String applicationId,
        String formCode,
        String status,
        String rejectedBy,
        String rejectionReason
) {
}
