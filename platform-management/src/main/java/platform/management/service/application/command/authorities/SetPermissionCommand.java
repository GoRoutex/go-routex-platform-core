package platform.management.service.application.command.authorities;

import lombok.Builder;

import java.util.Set;

@Builder
public record SetPermissionCommand(
        String roleId,
        Set<String> authoritiesCode,
        String requestId,
        String requestDateTime,
        String channel
) {
}
