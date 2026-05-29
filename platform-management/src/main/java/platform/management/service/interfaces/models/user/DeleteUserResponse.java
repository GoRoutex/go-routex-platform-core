package platform.management.service.interfaces.models.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import platform.management.service.domain.user.model.UserStatus;
import platform.core.common.service.api.BaseResponse;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class DeleteUserResponse extends BaseResponse<DeleteUserResponse.DeleteUserResponseData> {

    @Getter
    @Setter
    @NoArgsConstructor
    @SuperBuilder
    public static class DeleteUserResponseData {
        private String id;
        private UserStatus status;
    }
}
