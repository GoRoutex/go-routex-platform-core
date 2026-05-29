package platform.merchant.service.infrastructure.persistence.jpa.merchant.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import platform.merchant.service.infrastructure.persistence.jpa.merchant.entity.MerchantUserEntity;

import java.util.Optional;

public interface MerchantUserEntityRepository extends JpaRepository<MerchantUserEntity, String> {

    Optional<MerchantUserEntity> findByUserId(String userId);
}
