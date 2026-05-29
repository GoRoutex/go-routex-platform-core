package platform.driver.service.application.dto.passengers;

import lombok.Builder;
import platform.core.common.service.common.RequestContext;


@Builder
public record PassengerCheckinCommand(
        RequestContext context,
        String ticketId,
        String performedBy,
        String deviceId
) {
}
