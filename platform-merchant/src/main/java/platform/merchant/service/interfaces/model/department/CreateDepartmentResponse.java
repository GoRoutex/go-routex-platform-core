package platform.merchant.service.interfaces.model.department;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import platform.core.common.service.api.BaseResponse;
import platform.merchant.service.domain.department.DepartmentStatus;
import platform.merchant.service.domain.department.DepartmentType;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class CreateDepartmentResponse extends BaseResponse<CreateDepartmentResponse.CreateDepartmentResponseData> {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    public static class CreateDepartmentResponseData {
        private String id;
        private String name;
        private DepartmentType type;
        private String address;
        private String wardId;
        private String wardName;
        private String provinceId;
        private String provinceName;
        private String openingTime;
        private String closingTime;
        private String onlineOpeningTime;
        private String onlineClosingTime;
        private Double latitude;
        private Double longitude;
        private DepartmentStatus status;
    }
}
