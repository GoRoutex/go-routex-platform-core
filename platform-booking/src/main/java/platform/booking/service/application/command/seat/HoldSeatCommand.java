package platform.booking.service.application.command.seat;

import lombok.Builder;
import platform.core.common.service.common.RequestContext;

import java.util.List;

@Builder
public record HoldSeatCommand(
        RequestContext context,
        String creator,
        String tripId,
        List<String> seatNos,
        String holdBy,
        String customerName,
        String customerPhone,
        String customerEmail,
        String pickupType,
        String pickupStopId,
        String pickupAddress,
        String dropOffType,
        String dropOffStopId,
        String dropOffAddress
) {
}
