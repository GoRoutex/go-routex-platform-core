package platform.management.service.application.command.seat;

import lombok.Builder;
import platform.management.service.application.command.common.RequestContext;


@Builder
public record SearchSeatCommand(
        RequestContext context,
        int pageNumber,
        int pageSize,
        String tripId
) {
}
