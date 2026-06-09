package platform.management.service.infrastructure.persistence.adapter.seat;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import platform.core.common.service.domain.seat.SeatStatus;
import platform.merchant.service.infrastructure.persistence.jpa.seat.projection.TripSeatAvailabilityProjection;
import platform.merchant.service.infrastructure.persistence.jpa.seat.repository.TripSeatEntityRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Primary
@RequiredArgsConstructor
public class TripSeatAvailabilityAdapter implements platform.core.common.service.domain.seat.port.TripSeatAvailabilityPort {

    private final TripSeatEntityRepository tripSeatEntityRepository;

    @Override
    public Map<String, Long> countAvailableSeats(List<String> tripIds) {
        return tripSeatEntityRepository.countAvailableSeatsByTripIdAndStatus(tripIds, SeatStatus.AVAILABLE.name()).stream()
                .collect(Collectors.toMap(TripSeatAvailabilityProjection::getTripId, TripSeatAvailabilityProjection::getAvailableSeat));
    }
}
