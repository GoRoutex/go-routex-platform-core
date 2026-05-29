package platform.merchant.service.infrastructure.persistence.jpa.customer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import platform.merchant.service.infrastructure.persistence.jpa.customer.entity.CustomerEntity;

import java.util.Optional;

public interface CustomerEntityRepository extends JpaRepository<CustomerEntity, String> {
    Optional<CustomerEntity> findByUserId(String userId);
}
