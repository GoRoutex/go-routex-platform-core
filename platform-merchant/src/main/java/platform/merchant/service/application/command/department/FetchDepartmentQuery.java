package platform.merchant.service.application.command.department;

import lombok.Builder;
import platform.core.common.service.common.RequestContext;

@Builder
public record FetchDepartmentQuery(
        String pageSize,
        String pageNumber,
        String merchantId,
        String provinceId,
        RequestContext context
) {
}
