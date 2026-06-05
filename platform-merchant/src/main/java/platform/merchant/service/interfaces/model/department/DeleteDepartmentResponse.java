package platform.merchant.service.interfaces.model.department;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import platform.core.common.service.api.BaseResponse;
import platform.merchant.service.domain.department.DepartmentStatus;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class DeleteDepartmentResponse extends BaseResponse<DeleteDepartmentResponse.DeleteDepartmentResponseData> {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    public static class DeleteDepartmentResponseData {
        private String id;
        private DepartmentStatus status;
    }
}
