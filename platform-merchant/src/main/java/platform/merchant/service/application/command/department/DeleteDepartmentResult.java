package platform.merchant.service.application.command.department;

import lombok.Builder;
import platform.merchant.service.domain.department.DepartmentStatus;

@Builder
public record DeleteDepartmentResult(
        String id,
        DepartmentStatus status
) {
}
