package platform.merchant.service.application.command.vehicle;

import lombok.Builder;
import platform.core.common.service.domain.vehicle.VehicleStatus;
import platform.core.common.service.domain.vehicle.VehicleTemplateCategory;
import platform.merchant.service.domain.vehicle.VehicleTemplateType;

import java.util.List;

@Builder
public record FetchVehiclesResult(
        List<FetchVehicleItemResult> items,
        int pageNumber,
        int pageSize,
        long totalElements,
        int totalPages
) {

    @Builder
    public record FetchVehicleItemResult(
            String id,
            String templateId,
            String creator,
            VehicleStatus status,
            VehicleTemplateCategory category,
            VehicleTemplateType type,
            String vehiclePlate,
            Long seatCapacity,
            Boolean hasFloor,
            String manufacturer
    ) {
    }
}
