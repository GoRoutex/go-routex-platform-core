package platform.management.service.application.command.authorities;

import lombok.Builder;

@Builder
public record AddPermissionCommand(
        String code,
        String name,
        String description,
        String creator,
        boolean enabled,
        String requestId,
        String requestDateTime,
        String channel
) {
}
