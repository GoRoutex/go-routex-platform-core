package platform.merchant.service.infrastructure.persistence.jpa.authorities.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import platform.merchant.service.infrastructure.persistence.jpa.authorities.entity.UserRoleIdEntity;
import platform.merchant.service.infrastructure.persistence.jpa.authorities.entity.UserRolesEntity;

public interface UserRolesEntityRepository extends JpaRepository<UserRolesEntity, UserRoleIdEntity> {
    void deleteByIdUserId(String userId);
}
