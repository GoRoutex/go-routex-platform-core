package platform.merchant.service.interfaces.model.route;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import platform.core.common.service.api.BaseRequest;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class UpdateRouteRequest extends BaseRequest {

    private String routeId;
    private String creator;

    @Valid
    @NotNull
    private UpdateRouteRequestData data;

    @Getter
    @Setter
    @NoArgsConstructor
    @SuperBuilder
    public static class UpdateRouteRequestData {
        @NotBlank
        @NotNull
        private String originName;
        @NotBlank
        @NotNull
        private String destinationName;
        private String originDepartmentId;
        private String destinationDepartmentId;
        private Long duration;

        @Pattern(regexp = "^(ACTIVE|SUSPENDED|INACTIVE)$", message = "only allowed for ACTIVE, SUSPENDED and ACTIVE")
        private String status;
        private List<UpdateRoutePointRequest> routePoints;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @SuperBuilder
    @NoArgsConstructor
    public static class UpdateRoutePointRequest {
        private Integer stopOrder;
        private String note;
        private String departmentId;
        private String stopName;
        private String stopAddress;
        private String stopCity;
        private Double stopLatitude;
        private Double stopLongitude;
        private Integer timeAtDepartment;
    }
}
