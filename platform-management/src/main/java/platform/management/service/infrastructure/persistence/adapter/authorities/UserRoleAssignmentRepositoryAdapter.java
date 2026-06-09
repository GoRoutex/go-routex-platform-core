package platform.management.service.infrastructure.persistence.adapter.authorities;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import platform.management.service.domain.authorities.model.UserRoleAssignment;
import platform.management.service.domain.authorities.port.UserRoleAssignmentRepositoryPort;
import platform.management.service.infrastructure.persistence.jpa.role.entity.UserRoleEntityId;
import platform.management.service.infrastructure.persistence.jpa.role.entity.UserRolesEntity;
import platform.management.service.infrastructure.persistence.jpa.role.repository.UserRolesEntityRepository;

@Component
@RequiredArgsConstructor
public class UserRoleAssignmentRepositoryAdapter implements UserRoleAssignmentRepositoryPort {

    private final UserRolesEntityRepository userRolesEntityRepository;

    @Override
    public boolean exists(String userId, String roleId) {
        return userRolesEntityRepository.existsById(UserRoleEntityId.builder()
                .userId(userId)
                .roleId(roleId)
                .build());
    }

    @Override
    public void save(UserRoleAssignment assignment) {
        userRolesEntityRepository.save(UserRolesEntity.builder()
                .id(UserRoleEntityId.builder()
                        .userId(assignment.getUserId())
                        .roleId(assignment.getRoleId())
                        .build())
                .assignedAt(assignment.getAssignedAt())
                .build());
    }

    @Override
    @Transactional
    public void deleteByUserId(String userId) {
        userRolesEntityRepository.deleteByIdUserId(userId);
    }
}
