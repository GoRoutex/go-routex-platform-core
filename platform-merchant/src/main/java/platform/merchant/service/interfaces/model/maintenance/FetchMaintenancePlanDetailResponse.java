package platform.merchant.service.interfaces.model.maintenance;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import platform.core.common.service.api.BaseResponse;
import platform.core.common.service.domain.vehicle.VehicleStatus;
import platform.core.common.service.domain.vehicle.VehicleTemplateCategory;
import platform.merchant.service.domain.maintenance.MaintenancePlanStatus;
import platform.merchant.service.domain.maintenance.MaintenancePlanType;
import platform.merchant.service.domain.vehicle.VehicleTemplateType;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class FetchMaintenancePlanDetailResponse extends BaseResponse<FetchMaintenancePlanDetailResponse.FetchMaintenancePlanDetailResponseData> {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    public static class MaintenancePlanVehicleDetailResponseData {
        private String id;
        private String templateId;
        private VehicleStatus status;
        private VehicleTemplateCategory category;
        private VehicleTemplateType type;
        private String vehiclePlate;
        private Long seatCapacity;
        private Boolean hasFloor;
        private String manufacturer;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    public static class FetchMaintenancePlanDetailResponseData {
        private String id;
        private String merchantId;
        private MaintenancePlanVehicleDetailResponseData vehicle;
        private String code;
        private String title;
        private String description;
        private MaintenancePlanType type;
        private MaintenancePlanStatus status;
        private LocalDate plannedDate;
        private LocalDate dueDate;
        private LocalDate completedDate;
        private Long currentOdometerKm;
        private Long targetOdometerKm;
        private BigDecimal estimatedCost;
        private BigDecimal actualCost;
        private String serviceProvider;
        private String note;
    }
}
