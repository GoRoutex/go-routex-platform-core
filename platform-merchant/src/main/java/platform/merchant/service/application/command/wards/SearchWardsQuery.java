package platform.merchant.service.application.command.wards;

import lombok.Builder;
import platform.core.common.service.common.RequestContext;

@Builder
public record SearchWardsQuery(
        String keyword,
        String provinceId,
        int page,
        int size,
        RequestContext context
) {
}
