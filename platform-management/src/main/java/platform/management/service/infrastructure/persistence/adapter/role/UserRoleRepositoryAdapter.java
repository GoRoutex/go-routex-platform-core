package platform.management.service.infrastructure.persistence.adapter.role;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import platform.management.service.domain.role.model.UserRoles;
import platform.management.service.domain.role.port.UserRoleRepositoryPort;
import platform.management.service.infrastructure.persistence.jpa.role.repository.UserRolesEntityRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UserRoleRepositoryAdapter implements UserRoleRepositoryPort {

    private final UserRolesEntityRepository userRoleJpaRepository;
    private final RolePersistenceMapper rolePersistenceMapper;

    @Override
    public UserRoles save(UserRoles userRoles) {
        return rolePersistenceMapper.toDomain(userRoleJpaRepository.save(rolePersistenceMapper.toEntity(userRoles)));
    }

    @Override
    public List<UserRoles> findByUserId(String userId) {
        return userRoleJpaRepository.findByIdUserId(userId).stream()
                .map(rolePersistenceMapper::toDomain)
                .toList();
    }
}
