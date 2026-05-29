package platform.management.service.application.command.merchant;

import lombok.Builder;
import platform.management.service.application.command.common.RequestContext;

@Builder
public record FetchMerchantDetailQuery(
        RequestContext context,
        String merchantId
) {
}
