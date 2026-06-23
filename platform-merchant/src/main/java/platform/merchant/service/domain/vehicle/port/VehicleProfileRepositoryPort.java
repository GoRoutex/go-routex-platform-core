package platform.merchant.service.domain.vehicle.port;


import platform.core.common.service.application.command.common.PagedResult;
import platform.core.common.service.domain.vehicle.VehicleStatus;
import platform.merchant.service.domain.vehicle.model.VehicleProfile;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface VehicleProfileRepositoryPort {
    boolean existsByVehiclePlate(String vehiclePlate);

    boolean existsByVehiclePlate(String vehiclePlate, String merchantId);

    Optional<VehicleProfile> findById(String id);

    Optional<VehicleProfile> findById(String id, String merchantId);

    List<VehicleProfile> findByMerchantId(String merchantId);

    List<VehicleProfile> findByTemplateId(String templateId, String merchantId);

    void save(VehicleProfile vehicleProfile);

    void saveAll(List<VehicleProfile> vehicleProfiles);

    PagedResult<VehicleProfile> fetch(int pageNumber, int pageSize);

    PagedResult<VehicleProfile> fetch(String merchantId, VehicleStatus status, int pageNumber, int pageSize);

    PagedResult<VehicleProfile> fetch(String merchantId, int pageNumber, int pageSize);

    Map<String, VehicleProfile> findByIds(List<String> vehicleIds);

    Map<String, VehicleProfile> findByIds(List<String> vehicleIds, String merchantId);

    List<VehicleProfile> findByIdIn(Set<String> vehicleIds);
}
