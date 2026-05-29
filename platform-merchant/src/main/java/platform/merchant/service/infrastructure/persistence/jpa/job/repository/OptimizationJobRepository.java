package platform.merchant.service.infrastructure.persistence.jpa.job.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import platform.merchant.service.infrastructure.persistence.jpa.job.entity.OptimizationJobEntity;

import java.util.Optional;

@Repository
public interface OptimizationJobRepository extends JpaRepository<OptimizationJobEntity, String> {
    Optional<OptimizationJobEntity> findByIdAndMerchantId(String id, String merchantId);
}
