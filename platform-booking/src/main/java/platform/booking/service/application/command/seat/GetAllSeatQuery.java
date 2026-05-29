package platform.booking.service.application.command.seat;

import lombok.Builder;
import platform.core.common.service.common.RequestContext;

@Builder
public record GetAllSeatQuery(
        RequestContext metadata,
        String routeId
) {
}
