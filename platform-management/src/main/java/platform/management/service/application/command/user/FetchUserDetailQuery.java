package platform.management.service.application.command.user;

import lombok.Builder;
import platform.management.service.application.command.common.RequestContext;

@Builder
public record FetchUserDetailQuery(
        RequestContext context,
        String userId
) {
}
