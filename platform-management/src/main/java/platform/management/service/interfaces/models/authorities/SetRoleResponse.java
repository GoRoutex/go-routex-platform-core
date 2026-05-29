package platform.management.service.interfaces.models.authorities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import platform.core.common.service.api.BaseResponse;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class SetRoleResponse extends BaseResponse<SetRoleResponse.SetRoleResponseData> {

    @Getter
    @Setter
    @NoArgsConstructor
    @SuperBuilder
    public static class SetRoleResponseData {
        private String userId;
        private String roleId;
        private OffsetDateTime assignedAt;
    }
}
