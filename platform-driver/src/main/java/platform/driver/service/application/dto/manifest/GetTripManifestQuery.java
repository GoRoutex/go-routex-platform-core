package platform.driver.service.application.dto.manifest;

import lombok.Builder;
import platform.core.common.service.common.RequestContext;

@Builder
public record GetTripManifestQuery(
        RequestContext context,
        String routeId) {
}

