package platform.merchant.service.infrastructure.persistence.adapter.authorities;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import platform.merchant.service.domain.authorities.model.UserRoleAssignment;
import platform.merchant.service.domain.authorities.port.UserRoleAssignmentRepositoryPort;
import platform.merchant.service.infrastructure.persistence.jpa.authorities.entity.UserRoleIdEntity;
import platform.merchant.service.infrastructure.persistence.jpa.authorities.entity.UserRolesEntity;
import platform.merchant.service.infrastructure.persistence.jpa.authorities.repository.UserRolesEntityRepository;

@Component
@RequiredArgsConstructor
public class UserRoleAssignmentRepositoryAdapter implements UserRoleAssignmentRepositoryPort {

    private final UserRolesEntityRepository userRolesEntityRepository;

    @Override
    public boolean exists(String userId, String roleId) {
        return userRolesEntityRepository.existsById(UserRoleIdEntity.builder()
                .userId(userId)
                .roleId(roleId)
                .build());
    }

    @Override
    public void save(UserRoleAssignment assignment) {
        userRolesEntityRepository.save(UserRolesEntity.builder()
                .id(UserRoleIdEntity.builder()
                        .userId(assignment.getUserId())
                        .roleId(assignment.getRoleId())
                        .build())
                .assignedAt(assignment.getAssignedAt())
                .build());
    }

    @Override
    public void deleteByUserId(String userId) {
        userRolesEntityRepository.deleteByIdUserId(userId);
    }
}
