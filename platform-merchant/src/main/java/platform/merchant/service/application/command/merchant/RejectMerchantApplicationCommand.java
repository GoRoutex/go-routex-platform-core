package platform.merchant.service.application.command.merchant;

import lombok.Builder;
import platform.core.common.service.common.RequestContext;

@Builder
public record RejectMerchantApplicationCommand(
        RequestContext context,
        String applicationFormId,
        String rejectedBy,
        String rejectionReason
) {
}
