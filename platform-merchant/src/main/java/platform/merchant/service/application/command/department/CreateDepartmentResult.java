package platform.merchant.service.application.command.department;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import platform.merchant.service.domain.department.DepartmentStatus;
import platform.merchant.service.domain.department.DepartmentType;

@Builder
public record CreateDepartmentResult(
        String id,
        String name,
        DepartmentType type,
        String address,
        String wardId,
        String wardName,
        String provinceId,
        String provinceName,
        String onlineOpeningTime,
        String onlineClosingTime,
        String openingTime,
        String closingTime,
        Double latitude,
        Double longitude,
        DepartmentStatus status
) {
}
