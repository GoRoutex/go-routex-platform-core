package platform.merchant.service.application.command.department;

import lombok.Builder;
import platform.core.common.service.common.RequestContext;

@Builder
public record DeleteDepartmentCommand(
        RequestContext context,
        String merchantId,
        String id
) {
}
