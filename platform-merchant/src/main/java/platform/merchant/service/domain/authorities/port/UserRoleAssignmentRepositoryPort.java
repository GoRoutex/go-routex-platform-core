package platform.merchant.service.domain.authorities.port;

import platform.merchant.service.domain.authorities.model.UserRoleAssignment;

public interface UserRoleAssignmentRepositoryPort {
    boolean exists(String userId, String roleId);

    void save(UserRoleAssignment assignment);

    void deleteByUserId(String userId);
}
