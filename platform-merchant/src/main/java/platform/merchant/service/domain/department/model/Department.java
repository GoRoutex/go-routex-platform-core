package platform.merchant.service.domain.department.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import platform.merchant.service.domain.department.DepartmentStatus;
import platform.merchant.service.domain.department.DepartmentType;

import java.time.OffsetDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Department {
    private String id;
    private String name;
    private String merchantId;
    private DepartmentType type;
    private String address;
    private String wardId;
    private String wardName;
    private String provinceId;
    private String provinceName;
    private String note;
    private String openingTime;
    private String closingTime;
    private String onlineOpeningTime;
    private String onlineClosingTime;
    private Double latitude;
    private Double longitude;
    private DepartmentStatus status;
    private OffsetDateTime createdAt;
    private String createdBy;
    private OffsetDateTime updatedAt;
    private String updatedBy;
}
