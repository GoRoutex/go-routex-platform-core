package platform.merchant.service.interfaces.model.maintenance;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import platform.merchant.service.domain.maintenance.MaintenancePlanStatus;
import platform.merchant.service.domain.maintenance.MaintenancePlanType;
import platform.core.common.service.domain.vehicle.VehicleStatus;
import platform.core.common.service.domain.vehicle.VehicleTemplateCategory;
import platform.core.common.service.domain.vehicle.VehicleTemplateType;
import platform.core.common.service.api.BaseResponse;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class FetchMaintenancePlanResponse extends BaseResponse<FetchMaintenancePlanResponse.FetchMaintenancePlanResponsePage> {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    public static class FetchMaintenancePlanResponsePage {
        private List<FetchMaintenancePlanResponseData> items;
        private Pagination pagination;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    public static class MaintenancePlanVehicleResponseData {
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
    public static class FetchMaintenancePlanResponseData {
        private String id;
        private MaintenancePlanVehicleResponseData vehicle;
        private String code;
        private String title;
        private MaintenancePlanType type;
        private MaintenancePlanStatus status;
        private LocalDate plannedDate;
        private LocalDate dueDate;
        private Long targetOdometerKm;
        private BigDecimal estimatedCost;
        private String serviceProvider;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    public static class Pagination {
        private int pageNumber;
        private int pageSize;
        private long totalElements;
        private int totalPages;
    }
}
