package platform.management.service.application.command.merchant;

import lombok.Builder;
import platform.core.common.service.common.RequestContext;

@Builder
public record FetchPendingMerchantApplicationFormsQuery(
        String status,
        String pageSize,
        String pageNumber,
        RequestContext context
) {
}
