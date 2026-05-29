package platform.merchant.service.infrastructure.persistence.jpa.maintenance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import platform.merchant.service.infrastructure.persistence.jpa.maintenance.entity.MaintenancePlanEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface MaintenancePlanEntityRepository extends JpaRepository<MaintenancePlanEntity, String>, JpaSpecificationExecutor<MaintenancePlanEntity> {
    Optional<MaintenancePlanEntity> findByIdAndMerchantId(String id, String merchantId);

    List<MaintenancePlanEntity> findByVehicleIdAndMerchantId(String vehicleId, String merchantId);

    boolean existsByCodeAndMerchantId(String code, String merchantId);
}
