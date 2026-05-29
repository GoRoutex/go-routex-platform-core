package platform.merchant.service.infrastructure.persistence.adapter.maintenance;

import org.springframework.stereotype.Component;
import platform.merchant.service.domain.maintenance.model.MaintenancePlan;
import platform.merchant.service.infrastructure.persistence.jpa.maintenance.entity.MaintenancePlanEntity;

@Component
public class MaintenancePlanPersistenceMapper {
    public MaintenancePlan toDomain(MaintenancePlanEntity entity) {
        if (entity == null) {
            return null;
        }

        return MaintenancePlan.builder()
                .id(entity.getId())
                .merchantId(entity.getMerchantId())
                .vehicleId(entity.getVehicleId())
                .code(entity.getCode())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .type(entity.getType())
                .status(entity.getStatus())
                .plannedDate(entity.getPlannedDate())
                .dueDate(entity.getDueDate())
                .completedDate(entity.getCompletedDate())
                .currentOdometerKm(entity.getCurrentOdometerKm())
                .targetOdometerKm(entity.getTargetOdometerKm())
                .estimatedCost(entity.getEstimatedCost())
                .actualCost(entity.getActualCost())
                .serviceProvider(entity.getServiceProvider())
                .note(entity.getNote())
                .createdAt(entity.getCreatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedAt(entity.getUpdatedAt())
                .updatedBy(entity.getUpdatedBy())
                .build();
    }

    public MaintenancePlanEntity toEntity(MaintenancePlan domain) {
        return MaintenancePlanEntity.builder()
                .id(domain.getId())
                .merchantId(domain.getMerchantId())
                .vehicleId(domain.getVehicleId())
                .code(domain.getCode())
                .title(domain.getTitle())
                .description(domain.getDescription())
                .type(domain.getType())
                .status(domain.getStatus())
                .plannedDate(domain.getPlannedDate())
                .dueDate(domain.getDueDate())
                .completedDate(domain.getCompletedDate())
                .currentOdometerKm(domain.getCurrentOdometerKm())
                .targetOdometerKm(domain.getTargetOdometerKm())
                .estimatedCost(domain.getEstimatedCost())
                .actualCost(domain.getActualCost())
                .serviceProvider(domain.getServiceProvider())
                .note(domain.getNote())
                .createdAt(domain.getCreatedAt())
                .createdBy(domain.getCreatedBy())
                .updatedAt(domain.getUpdatedAt())
                .updatedBy(domain.getUpdatedBy())
                .build();
    }
}
