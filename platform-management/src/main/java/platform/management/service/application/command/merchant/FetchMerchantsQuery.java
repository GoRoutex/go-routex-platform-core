package platform.management.service.application.command.merchant;

import lombok.Builder;
import platform.management.service.application.command.common.RequestContext;

@Builder
public record FetchMerchantsQuery(
        String pageSize,
        String pageNumber,
        RequestContext context
) {
}
