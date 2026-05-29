package platform.management.service.application.command.authorities;

import lombok.Builder;

@Builder
public record AddRoleResult(
        String code,
        String name,
        String creator,
        String description
) {
}
