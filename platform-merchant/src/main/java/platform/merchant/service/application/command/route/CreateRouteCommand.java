package platform.merchant.service.application.command.route;

import lombok.Builder;
import platform.core.common.service.common.RequestContext;

import java.util.List;

@Builder
public record  CreateRouteCommand(
        RequestContext context,
        String merchantId,
        String creator,
        String destinationName,
        String originName,
        String originDepartmentId,
        String destinationDepartmentId,
        Long duration,
        List<RoutePointCommand> routePoints
) {
}
