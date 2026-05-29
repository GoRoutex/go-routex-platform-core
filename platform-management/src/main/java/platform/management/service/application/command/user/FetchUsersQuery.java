package platform.management.service.application.command.user;

import lombok.Builder;
import platform.management.service.application.command.common.RequestContext;

@Builder
public record FetchUsersQuery(
        String pageSize,
        String pageNumber,
        RequestContext context
) {
}
