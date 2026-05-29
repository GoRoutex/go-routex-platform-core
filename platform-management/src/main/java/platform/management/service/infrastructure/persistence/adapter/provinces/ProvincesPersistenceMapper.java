package platform.management.service.infrastructure.persistence.adapter.provinces;

import org.springframework.stereotype.Component;
import platform.management.service.domain.provinces.model.Province;
import platform.management.service.infrastructure.persistence.jpa.provinces.entity.ProvincesEntity;

@Component
public class ProvincesPersistenceMapper {
    public Province toDomain(ProvincesEntity entity) {
        if (entity == null) return null;
        return Province.builder()
                .id(entity.getId())
                .name(entity.getName())
                .code(entity.getCode())
                .createdAt(entity.getCreatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedAt(entity.getUpdatedAt())
                .updatedBy(entity.getUpdatedBy())
                .build();

    }

    public ProvincesEntity toEntity(Province province) {
        if (province == null) return null;
        return ProvincesEntity.builder()
                .id(province.getId())
                .name(province.getName())
                .code(province.getCode())
                .createdAt(province.getCreatedAt())
                .createdBy(province.getCreatedBy())
                .updatedAt(province.getUpdatedAt())
                .updatedBy(province.getUpdatedBy())
                .build();
    }
}

