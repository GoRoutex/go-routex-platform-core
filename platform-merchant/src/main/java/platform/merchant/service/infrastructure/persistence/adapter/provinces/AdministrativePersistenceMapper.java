package platform.merchant.service.infrastructure.persistence.adapter.provinces;

import org.springframework.stereotype.Component;
import platform.merchant.service.domain.provinces.model.Ward;
import platform.merchant.service.infrastructure.persistence.jpa.provinces.entity.WardsEntity;

@Component
public class AdministrativePersistenceMapper {

    public Ward toDomain(WardsEntity entity) {
        if (entity == null) return null;
        return Ward.builder()
                .id(entity.getId())
                .name(entity.getName())
                .provinceId(entity.getProvinceId())
                .createdAt(entity.getCreatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedAt(entity.getUpdatedAt())
                .updatedBy(entity.getUpdatedBy())
                .build();
    }

    public WardsEntity toEntity(Ward domain) {
        if (domain == null) return null;
        return WardsEntity.builder()
                .id(domain.getId())
                .name(domain.getName())
                .provinceId(domain.getProvinceId())
                .createdAt(domain.getCreatedAt())
                .createdBy(domain.getCreatedBy())
                .updatedAt(domain.getUpdatedAt())
                .updatedBy(domain.getUpdatedBy())
                .build();
    }
}
