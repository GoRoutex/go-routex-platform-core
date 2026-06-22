package platform.merchant.service.application.command.department;

import lombok.Builder;
import platform.core.common.service.common.RequestContext;
import platform.merchant.service.domain.department.DepartmentStatus;

@Builder
public record FetchDepartmentQuery(
        String pageSize,
        String pageNumber,
        String merchantId,
        String provinceId,
        DepartmentStatus status,
        RequestContext context
) {
}
