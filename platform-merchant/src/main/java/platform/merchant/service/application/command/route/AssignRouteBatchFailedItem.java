package platform.merchant.service.application.command.route;

import lombok.Builder;

@Builder
public record AssignRouteBatchFailedItem(
        String tripId,
        String driverId,
        String vehicleId,
        String errorCode,
        String errorMessage
) {
}
