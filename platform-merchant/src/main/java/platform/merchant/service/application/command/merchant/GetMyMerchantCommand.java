package platform.merchant.service.application.command.merchant;

import lombok.Builder;
import platform.core.common.service.common.RequestContext;

@Builder
public record GetMyMerchantCommand(
    RequestContext context,
    String merchantId
) {
}
