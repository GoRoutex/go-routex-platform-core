package platform.merchant.service.interfaces.model.department;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import platform.core.common.service.api.BaseRequest;
import platform.merchant.service.domain.department.DepartmentStatus;
import platform.merchant.service.domain.department.DepartmentType;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class UpdateDepartmentRequest extends BaseRequest {

    @Valid
    @NotNull
    private UpdateDepartmentRequestData data;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    public static class UpdateDepartmentRequestData {
        @NotNull
        @NotBlank
        private String id;
        private String name;
        private DepartmentType type;
        private String address;
        private String wardId;
        private String provinceId;
        private String openingTime;
        private String closingTime;
        private String onlineOpeningTime;
        private String onlineClosingTime;
        private Double latitude;
        private Double longitude;
        private DepartmentStatus status;
    }
}

