package platform.merchant.service.infrastructure.persistence.adapter.department;

import org.springframework.stereotype.Component;
import platform.merchant.service.domain.department.model.Department;
import platform.merchant.service.infrastructure.persistence.jpa.department.entity.DepartmentEntity;

@Component
public class DepartmentPersistenceMapper {
    public Department toDomain(DepartmentEntity departmentEntity) {
        if(departmentEntity == null) {
            return null;
        }

        return Department.builder()
                .id(departmentEntity.getId())
                .merchantId(departmentEntity.getMerchantId())
                .name(departmentEntity.getName())
                .type(departmentEntity.getType())
                .address(departmentEntity.getAddress())
                .wardId(departmentEntity.getWardId())
                .wardName(departmentEntity.getWardName())
                .provinceId(departmentEntity.getProvinceId())
                .provinceName(departmentEntity.getProvinceName())
                .note(departmentEntity.getNote())
                .openingTime(departmentEntity.getOpeningTime())
                .closingTime(departmentEntity.getClosingTime())
                .onlineOpeningTime(departmentEntity.getOnlineOpeningTime())
                .onlineClosingTime(departmentEntity.getOnlineClosingTime())
                .latitude(departmentEntity.getLatitude())
                .longitude(departmentEntity.getLongitude())
                .status(departmentEntity.getStatus())
                .createdAt(departmentEntity.getCreatedAt())
                .createdBy(departmentEntity.getCreatedBy())
                .updatedAt(departmentEntity.getUpdatedAt())
                .updatedBy(departmentEntity.getUpdatedBy())
                .build();
    }

    public DepartmentEntity toEntity(Department department) {
        if(department == null) {
            return null;
        }

        return DepartmentEntity.builder()
                .id(department.getId())
                .merchantId(department.getMerchantId())
                .name(department.getName())
                .type(department.getType())
                .address(department.getAddress())
                .wardId(department.getWardId())
                .wardName(department.getWardName())
                .provinceId(department.getProvinceId())
                .provinceName(department.getProvinceName())
                .note(department.getNote())
                .openingTime(department.getOpeningTime())
                .closingTime(department.getClosingTime())
                .onlineOpeningTime(department.getOnlineOpeningTime())
                .onlineClosingTime(department.getOnlineClosingTime())
                .latitude(department.getLatitude())
                .longitude(department.getLongitude())
                .status(department.getStatus())
                .createdAt(department.getCreatedAt())
                .createdBy(department.getCreatedBy())
                .updatedAt(department.getUpdatedAt())
                .updatedBy(department.getUpdatedBy())
                .build();
    }
}
