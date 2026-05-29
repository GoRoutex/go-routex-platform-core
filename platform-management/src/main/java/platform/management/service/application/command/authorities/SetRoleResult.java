package platform.management.service.application.command.authorities;

import lombok.Builder;

import java.time.OffsetDateTime;

@Builder
public record SetRoleResult(
        String userId,
        String roleId,
        OffsetDateTime assignedAt
) {
}
