package platform.booking.service.application.command.seat;

import lombok.Builder;
import platform.core.common.service.common.RequestContext;

import java.util.List;

@Builder
public record HoldRoundTripSeatCommand(
        RequestContext context,
        String creator,
        HoldRoundTripSeatLegCommand outboundTrip,
        HoldRoundTripSeatLegCommand returnTrip,
        String holdBy,
        String customerName,
        String customerPhone,
        String customerEmail
) {

    @Builder
    public record HoldRoundTripSeatLegCommand(
            String tripId,
            List<String> seatNos,
            String pickupType,
            String pickupStopId,
            String pickupAddress,
            String dropOffType,
            String dropOffStopId,
            String dropOffAddress
    ) {
    }
}
