package platform.management.service.infrastructure.persistence.jpa.role.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import platform.management.service.infrastructure.persistence.jpa.role.entity.UserRoleEntityId;
import platform.management.service.infrastructure.persistence.jpa.role.entity.UserRolesEntity;

import java.util.List;

public interface UserRolesEntityRepository extends JpaRepository<UserRolesEntity, UserRoleEntityId> {

    List<UserRolesEntity> findByIdUserId(String userId);

    void deleteByIdUserId(String userId);
}
