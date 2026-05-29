package platform.merchant.service.application.command.department;

import lombok.Builder;
import platform.core.common.service.common.RequestContext;

@Builder
public record GetDepartmentDetailQuery(
        RequestContext context,
        String merchantId,
        String departmentId
) {
}
