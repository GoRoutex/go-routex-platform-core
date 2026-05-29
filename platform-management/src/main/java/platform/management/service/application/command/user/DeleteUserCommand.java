package platform.management.service.application.command.user;

import lombok.Builder;
import platform.management.service.application.command.common.RequestContext;

@Builder
public record DeleteUserCommand(
        RequestContext context,
        String userId,
        String updatedBy
) {
}
