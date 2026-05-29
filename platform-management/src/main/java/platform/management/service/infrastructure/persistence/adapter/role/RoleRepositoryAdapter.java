package platform.management.service.infrastructure.persistence.adapter.role;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import platform.management.service.domain.authorities.model.RoleAggregate;
import platform.management.service.domain.role.port.RoleRepositoryPort;
import platform.management.service.infrastructure.persistence.jpa.role.entity.RolesEntity;
import platform.management.service.infrastructure.persistence.jpa.role.repository.RolesEntityRepository;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RoleRepositoryAdapter implements RoleRepositoryPort {

    private final RolesEntityRepository rolesEntityRepository;
    private final RolePersistenceMapper rolePersistenceMapper;

    @Override
    public Optional<RoleAggregate> findById(String id) {
        return rolesEntityRepository.findById(id).map(rolePersistenceMapper::toDomain);
    }

    @Override
    public Optional<RoleAggregate> findByCode(String code) {
        return rolesEntityRepository.findByCode(code).map(rolePersistenceMapper::toDomain);
    }

    @Override
    public boolean existsByCode(String code) {
        return rolesEntityRepository.existsByCode(code);
    }

    @Override
    public void save(RoleAggregate roleAggregate) {
        rolesEntityRepository.save(toEntity(roleAggregate));
    }

    private RoleAggregate toDomain(RolesEntity entity) {
        return RoleAggregate.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .name(entity.getName())
                .description(entity.getDescription())
                .enabled(entity.getEnabled())
                .createdAt(entity.getCreatedAt())
                .createdBy(entity.getCreatedBy())
                .build();
    }

    private RolesEntity toEntity(RoleAggregate roleAggregate) {
        return RolesEntity.builder()
                .id(roleAggregate.getId())
                .code(roleAggregate.getCode())
                .name(roleAggregate.getName())
                .description(roleAggregate.getDescription())
                .enabled(roleAggregate.getEnabled())
                .createdAt(roleAggregate.getCreatedAt())
                .createdBy(roleAggregate.getCreatedBy())
                .build();
    }
}
