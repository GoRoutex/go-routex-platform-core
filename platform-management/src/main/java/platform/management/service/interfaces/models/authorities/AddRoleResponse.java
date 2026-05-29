package platform.management.service.interfaces.models.authorities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import platform.core.common.service.api.BaseResponse;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class AddRoleResponse extends BaseResponse<AddRoleResponse.AddRoleResponseData> {

    @Getter
    @Setter
    @NoArgsConstructor
    @SuperBuilder
    public static class AddRoleResponseData {
        private String code;
        private String name;
        private String creator;
        private String description;
    }
}
