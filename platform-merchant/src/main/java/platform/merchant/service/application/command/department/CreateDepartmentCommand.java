package platform.merchant.service.application.command.department;


import lombok.Builder;
import platform.core.common.service.common.RequestContext;
import platform.merchant.service.domain.department.DepartmentStatus;
import platform.merchant.service.domain.department.DepartmentType;

@Builder
public record CreateDepartmentCommand(
        RequestContext context,
        String merchantId,
        String name,
        DepartmentType type,
        String address,
        String wardId,
        String provinceId,
        String openingTime,
        String closingTime,
        String onlineOpeningTime,
        String onlineClosingTime,
        Double latitude,
        Double longitude,
        DepartmentStatus status
) {
}
