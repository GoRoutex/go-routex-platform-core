package platform.management.service.infrastructure.persistence.adapter.authorities;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import platform.management.service.domain.authorities.model.PermissionProfile;
import platform.management.service.domain.authorities.port.PermissionRepositoryPort;
import platform.management.service.infrastructure.persistence.jpa.authorities.entity.AuthoritiesEntity;
import platform.management.service.infrastructure.persistence.jpa.authorities.repository.AuthoritiesEntityRepository;

import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class PermissionRepositoryAdapter implements PermissionRepositoryPort {

    private final AuthoritiesEntityRepository authoritiesEntityRepository;

    @Override
    public boolean existsByCode(String code) {
        return authoritiesEntityRepository.existsByCode(code);
    }

    @Override
    public List<PermissionProfile> findByCodes(Set<String> codes) {
        return authoritiesEntityRepository.findByCodeIn(codes).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public void save(PermissionProfile permissionProfile) {
        authoritiesEntityRepository.save(toEntity(permissionProfile));
    }

    private PermissionProfile toDomain(AuthoritiesEntity entity) {
        return PermissionProfile.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .name(entity.getName())
                .description(entity.getDescription())
                .enabled(entity.getEnabled())
                .createdAt(entity.getCreatedAt())
                .createdBy(entity.getCreatedBy())
                .build();
    }

    private AuthoritiesEntity toEntity(PermissionProfile profile) {
        return AuthoritiesEntity.builder()
                .id(profile.getId())
                .code(profile.getCode())
                .name(profile.getName())
                .description(profile.getDescription())
                .enabled(profile.getEnabled())
                .createdAt(profile.getCreatedAt())
                .createdBy(profile.getCreatedBy())
                .build();
    }
}
