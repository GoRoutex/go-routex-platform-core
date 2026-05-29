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

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class DeleteMaintenancePlanRequest extends BaseRequest {

    @Valid
    @NotNull
    private DeleteMaintenancePlanRequestData data;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    public static class DeleteMaintenancePlanRequestData {
        @NotBlank
        private String creator;
        @NotBlank
        private String maintenancePlanId;
    }
}
