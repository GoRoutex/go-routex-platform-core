package platform.merchant.service.infrastructure.persistence.jpa.driver.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import platform.merchant.service.domain.driver.DriverStatus;
import platform.merchant.service.domain.driver.OperationStatus;
import platform.merchant.service.infrastructure.persistence.jpa.driver.entity.DriverProfileEntity;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface DriverProfileEntityRepository extends JpaRepository<DriverProfileEntity, String> {
    Optional<DriverProfileEntity> findByUserId(String userId);

    Optional<DriverProfileEntity> findByIdAndMerchantId(String id, String merchantId);

    Optional<DriverProfileEntity> findByUserIdAndMerchantId(String userId, String merchantId);

    Optional<DriverProfileEntity> findByEmployeeCodeAndMerchantId(String employeeCode, String merchantId);

    boolean existsByUserIdAndMerchantId(String userId, String merchantId);

    boolean existsByEmployeeCodeAndMerchantId(String employeeCode, String merchantId);

    List<DriverProfileEntity> findByMerchantId(String merchantId);

    Page<DriverProfileEntity> findByMerchantId(String merchantId, Pageable pageable);

    Page<DriverProfileEntity> findByMerchantIdAndStatus(String merchantId, DriverStatus status, Pageable pageable);

    Page<DriverProfileEntity> findByMerchantIdAndOperationStatus(String merchantId, OperationStatus operationStatus, Pageable pageable);

    List<DriverProfileEntity> findByIdIn(Set<String> vehicleIds);
}
