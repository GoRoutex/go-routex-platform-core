package platform.management.service.application.command.merchant;

import lombok.Builder;
import platform.core.common.service.common.RequestContext;

@Builder
public record FetchMerchantDetailQuery(
        RequestContext context,
        String merchantId
) {
}
