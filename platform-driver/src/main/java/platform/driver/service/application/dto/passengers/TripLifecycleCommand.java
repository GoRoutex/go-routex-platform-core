package platform.driver.service.application.dto.passengers;

import lombok.Builder;
import platform.core.common.service.common.RequestContext;

@Builder
public record TripLifecycleCommand(
        RequestContext context,
        String tripId,
        String performedBy,
        String deviceId
) {
}
