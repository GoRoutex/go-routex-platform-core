package platform.merchant.service.interfaces.model.route;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class CreateRouteRequest extends BaseRequest {

    @Valid
    @NotNull
    private CreateRouteRequestData data;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    public static class CreateRouteRequestData {

        @NotBlank
        @NotNull
        private String creator;
        @NotBlank
        @NotNull
        private String originName;
        @NotBlank
        @NotNull
        private String destinationName;
        private String originDepartmentId;
        private String destinationDepartmentId;
        private Long duration;
        private List<RoutePoints> routePoints;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    public static class RoutePoints {
        private String stopOrder;
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
