package platform.merchant.service.application.command.trip;

import lombok.Builder;
import platform.core.common.service.common.RequestContext;

@Builder
public record DeleteTripCommand(
        RequestContext context,
        String tripId,
        String merchantId
) {
}
