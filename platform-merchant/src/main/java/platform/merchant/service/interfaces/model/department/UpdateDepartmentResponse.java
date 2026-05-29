package platform.merchant.service.interfaces.model.department;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import platform.merchant.service.domain.department.DepartmentStatus;
import platform.merchant.service.domain.department.DepartmentType;
import platform.core.common.service.api.BaseResponse;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class UpdateDepartmentResponse extends BaseResponse<UpdateDepartmentResponse.UpdateDepartmentResponseData> {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    public static class UpdateDepartmentResponseData {
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
