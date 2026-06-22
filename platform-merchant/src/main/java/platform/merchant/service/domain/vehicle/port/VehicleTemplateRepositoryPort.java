package platform.merchant.service.domain.vehicle.port;


import platform.core.common.service.application.command.common.PagedResult;
import platform.core.common.service.domain.vehicle.VehicleTemplateCategory;
import platform.core.common.service.domain.vehicle.VehicleTemplateStatus;
import platform.merchant.service.domain.vehicle.VehicleTemplateType;
import platform.merchant.service.domain.vehicle.model.VehicleTemplate;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface VehicleTemplateRepositoryPort {
    Optional<VehicleTemplate> findByCategoryAndType(String category, String type);

    Optional<VehicleTemplate> findByCategoryAndType(String category, String type, String merchantId);

    Optional<VehicleTemplate> findById(String id);

    Optional<VehicleTemplate> findById(String id, String merchantId);

    Optional<VehicleTemplate> findByIdIncludingInactive(String id, String merchantId);

    Map<String, VehicleTemplate> findByIds(List<String> ids);

    Map<String, VehicleTemplate> findByIdsIncludingInactive(List<String> ids);

    boolean existsByCode(String code, String merchantId);

    boolean existsByCategoryAndType(String category, String type, String merchantId);

    void save(VehicleTemplate template);

    PagedResult<VehicleTemplate> fetch(
            String merchantId,
            VehicleTemplateStatus status,
            VehicleTemplateCategory category,
            VehicleTemplateType type,
            int pageNumber,
            int pageSize
    );
}
