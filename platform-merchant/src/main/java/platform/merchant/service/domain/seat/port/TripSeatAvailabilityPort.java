package platform.core.common.service.domain.seat.port;

import java.util.List;
import java.util.Map;

public interface TripSeatAvailabilityPort {
    Map<String, Long> countAvailableSeats(List<String> tripIds);
}
