package platform.management.service.application.command.user;

import lombok.Builder;
import platform.management.service.domain.user.model.UserStatus;

@Builder
public record DeleteUserResult(
        String id,
        UserStatus status
) {
}
