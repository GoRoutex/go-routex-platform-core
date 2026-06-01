package platform.management.service.application.command.seat;

import lombok.Builder;
import platform.core.common.service.common.RequestContext;


@Builder
public record SearchSeatCommand(
        RequestContext context,
        int pageNumber,
        int pageSize,
        String tripId
) {
}
