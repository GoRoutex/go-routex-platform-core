package platform.booking.service.infrastructure.cache.mapper;


import org.springframework.stereotype.Component;
import platform.core.common.service.domain.seat.model.TripSeat;
import platform.core.common.service.infrastructure.redis.models.TripCacheSeat;

@Component
public class TripCacheMapper {

    public TripCacheSeat toCacheModel(TripSeat domain) {
        if(domain == null) {
            return null;
        }

        return TripCacheSeat.builder()
                .tripId(domain.getTripId())
                .seatId(domain.getId())
                .seatNo(domain.getSeatNo())
                .seatTemplateId(domain.getSeatTemplateId())
                .status(domain.getStatus())
                .build();
    }

    public TripSeat toDomain(TripCacheSeat cache) {
        if(cache == null) {
            return null;
        }

        return TripSeat.builder()
                .id(cache.getSeatId())
                .tripId(cache.getTripId())
                .seatNo(cache.getSeatNo())
                .status(cache.getStatus())
                .seatTemplateId(cache.getSeatTemplateId())
                .build();
    }
}
