package platform.merchant.service.interfaces.model.maintenance;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import platform.core.common.service.api.BaseRequest;
import platform.merchant.service.domain.maintenance.MaintenancePlanType;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class CreateMaintenancePlanRequest extends BaseRequest {

    @Valid
    @NotNull
    private CreateMaintenancePlanRequestData data;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    public static class CreateMaintenancePlanRequestData {
        @NotBlank
        private String creator;
        @NotBlank
        private String vehicleId;
        @NotBlank
        private String code;
        @NotBlank
        private String title;
        private String description;
        @NotNull
        private MaintenancePlanType type;
        private LocalDate plannedDate;
        private LocalDate dueDate;
        private Long currentOdometerKm;
        private Long targetOdometerKm;
        private BigDecimal estimatedCost;
        private String serviceProvider;
        private String note;
    }
}
