package platform.core.common.service.api;

import lombok.Builder;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Builder
public record TripBookingContextResponse(
        String tripId,
        String merchantId,
        String routeId,
        String vehicleId,
        String blueprintId,
        String driverId,
        String status,
        OffsetDateTime departureTime,
        OffsetDateTime estimatedArrivalTime,
        BigDecimal basePrice,
        String currency
) {
}
