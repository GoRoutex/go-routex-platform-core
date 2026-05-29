package platform.management.service.infrastructure.persistence.adapter.trip;


import org.springframework.stereotype.Component;
import platform.management.service.domain.trip.model.TripAggregate;
import platform.management.service.infrastructure.persistence.jpa.trip.entity.TripEntity;

@Component
public class TripAggregatePersistenceMapper {
    public TripEntity toEntity(TripAggregate aggregate) {
        if(aggregate == null) {
            return null;
        }

        return TripEntity.builder()
        		.id(aggregate.getId())
        		.routeId(aggregate.getRouteId())
        		.merchantId(aggregate.getMerchantId())
        		.tripCode(aggregate.getTripCode())
        		.departureTime(aggregate.getDepartureTime())
        		.rawDepartureTime(aggregate.getRawDepartureTime())
        		.rawDepartureDate(aggregate.getRawDepartureDate())
        		.status(aggregate.getStatus())
                .createdAt(aggregate.getCreatedAt())
                .createdBy(aggregate.getCreatedBy())
                .updatedAt(aggregate.getUpdatedAt())
                .updatedBy(aggregate.getUpdatedBy())
        		.build();
    }

    public TripAggregate toDomain(TripEntity entity) {
        if (entity == null) {
            return null;
        }
        return TripAggregate.builder()
                .id(entity.getId())
                .routeId(entity.getRouteId())
                .merchantId(entity.getMerchantId())
                .tripCode(entity.getTripCode())
                .departureTime(entity.getDepartureTime())
                .rawDepartureTime(entity.getRawDepartureTime())
                .rawDepartureDate(entity.getRawDepartureDate())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedAt(entity.getUpdatedAt())
                .updatedBy(entity.getUpdatedBy())
                .build();
    }
}
