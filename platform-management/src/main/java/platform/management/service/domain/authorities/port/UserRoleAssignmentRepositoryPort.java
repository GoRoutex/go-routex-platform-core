package platform.management.service.domain.authorities.port;

import platform.management.service.domain.authorities.model.UserRoleAssignment;

public interface UserRoleAssignmentRepositoryPort {
    boolean exists(String userId, String roleId);

    void save(UserRoleAssignment assignment);

    void deleteByUserId(String userId);
}
