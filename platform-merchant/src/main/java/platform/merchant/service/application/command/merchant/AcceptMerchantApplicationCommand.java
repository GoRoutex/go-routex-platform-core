package platform.merchant.service.application.command.merchant;

import lombok.Builder;
import platform.core.common.service.common.RequestContext;

import java.math.BigDecimal;

@Builder
public record AcceptMerchantApplicationCommand(
        RequestContext context,
        String applicationFormId,
        String approvedBy,
        BigDecimal commission
) {
}
