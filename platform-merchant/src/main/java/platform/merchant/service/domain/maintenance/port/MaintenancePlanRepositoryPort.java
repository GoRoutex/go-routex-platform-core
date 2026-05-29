package platform.merchant.service.domain.maintenance.port;

import platform.core.common.service.application.command.common.PagedResult;
import platform.merchant.service.domain.maintenance.MaintenancePlanStatus;
import platform.merchant.service.domain.maintenance.MaintenancePlanType;
import platform.merchant.service.domain.maintenance.model.MaintenancePlan;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MaintenancePlanRepositoryPort {
    Optional<MaintenancePlan> findById(String id);

    Optional<MaintenancePlan> findById(String id, String merchantId);

    List<MaintenancePlan> findByVehicleId(String vehicleId, String merchantId);

    boolean existsByCode(String code, String merchantId);

    void save(MaintenancePlan maintenancePlan);

    PagedResult<MaintenancePlan> fetch(
            String merchantId,
            String vehicleId,
            MaintenancePlanStatus status,
            MaintenancePlanType type,
            LocalDate fromPlannedDate,
            LocalDate toPlannedDate,
            int pageNumber,
            int pageSize
    );
}
