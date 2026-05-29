package platform.merchant.service.infrastructure.persistence.jpa.role.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import platform.merchant.service.infrastructure.persistence.jpa.role.entity.RolesEntity;

import java.util.Optional;

public interface RolesEntityRepository extends JpaRepository<RolesEntity, String> {
    Optional<RolesEntity> findByCode(String code);
}
