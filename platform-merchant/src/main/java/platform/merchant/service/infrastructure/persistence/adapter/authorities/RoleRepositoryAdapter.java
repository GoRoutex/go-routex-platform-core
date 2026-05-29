package platform.merchant.service.infrastructure.persistence.adapter.authorities;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import platform.merchant.service.domain.authorities.model.RoleAggregate;
import platform.merchant.service.domain.authorities.port.RoleRepositoryPort;
import platform.merchant.service.infrastructure.persistence.jpa.role.repository.RolesEntityRepository;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RoleRepositoryAdapter implements RoleRepositoryPort {

    private final RolesEntityRepository rolesEntityRepository;

    @Override
    public Optional<RoleAggregate> findByCode(String code) {
        return rolesEntityRepository.findByCode(code)
                .map(role -> RoleAggregate.builder()
                        .id(role.getId())
                        .code(role.getCode())
                        .name(role.getName())
                        .description(role.getDescription())
                        .enabled(role.getEnabled())
                        .createdAt(role.getCreatedAt())
                        .createdBy(role.getCreatedBy())
                        .updatedAt(role.getUpdatedAt())
                        .updatedBy(role.getUpdatedBy())
                        .build());
    }
}
