package platform.driver.service.application.dto.passengers;

import lombok.Builder;
import platform.core.common.service.domain.trip.TripStatus;

@Builder
public record TripLifecycleResult(
        String tripId,
        TripStatus tripStatus,
        int totalTickets,
        int boardedTickets,
        int completedTickets,
        int expiredTickets,
        int unchangedTickets
) {
}
