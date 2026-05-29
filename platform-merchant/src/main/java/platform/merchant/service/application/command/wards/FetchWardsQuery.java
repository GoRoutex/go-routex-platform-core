package platform.merchant.service.application.command.wards;

import lombok.Builder;
import platform.core.common.service.common.RequestContext;

@Builder
public record FetchWardsQuery(
        String provinceId,
        String pageSize,
        String pageNumber,
        RequestContext context
) {
}
