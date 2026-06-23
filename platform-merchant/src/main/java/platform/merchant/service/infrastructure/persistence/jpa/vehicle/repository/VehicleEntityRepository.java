package platform.merchant.service.infrastructure.persistence.jpa.vehicle.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import platform.merchant.service.infrastructure.persistence.jpa.vehicle.entity.VehicleEntity;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface VehicleEntityRepository extends JpaRepository<VehicleEntity, String> {
    boolean existsByVehiclePlate(String vehiclePlate);

    boolean existsByVehiclePlateAndMerchantId(String vehiclePlate, String merchantId);

    Optional<VehicleEntity> findByIdAndMerchantId(String id, String merchantId);

    List<VehicleEntity> findByIdIn(List<String> vehicleIds);

    List<VehicleEntity> findByIdIn(Set<String> vehicleIds);

    List<VehicleEntity> findByIdInAndMerchantId(List<String> vehicleIds, String merchantId);

    List<VehicleEntity> findByMerchantId(String merchantId);

    List<VehicleEntity> findByTemplateIdAndMerchantId(String templateId, String merchantId);

    @Query(value = """
            SELECT v.*
            FROM vehicle v
            WHERE v.merchant_id = :merchantId
              AND UPPER(TRIM(CAST(v.status AS TEXT))) = :status
            """,
            countQuery = """
                    SELECT COUNT(*)
                    FROM vehicle v
                    WHERE v.merchant_id = :merchantId
                      AND UPPER(TRIM(CAST(v.status AS TEXT))) = :status
                    """,
            nativeQuery = true)
    Page<VehicleEntity> findByMerchantIdAndStatus(
            @Param("merchantId") String merchantId,
            @Param("status") String status,
            Pageable pageable
    );

    Page<VehicleEntity> findByMerchantId(String merchantId, Pageable pageable);

    Page<VehicleEntity> findAll(Pageable pageable);
}
