package platform.merchant.service.domain.trip.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import platform.core.common.service.persistence.AbstractAuditingEntity;
import platform.core.common.service.domain.trip.TripStatus;

import java.time.OffsetDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public class TripAggregate extends AbstractAuditingEntity {
    private String id;
    private String routeId;
    private String creator;
    private String merchantId;
    private String tripCode;
    private OffsetDateTime departureTime;
    private String rawDepartureTime;
    private String rawDepartureDate;
    private TripStatus status;
}
