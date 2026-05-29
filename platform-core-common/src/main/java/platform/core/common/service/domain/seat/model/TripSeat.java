package platform.core.common.service.domain.seat.model;

import platform.core.common.service.domain.seat.SeatStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import platform.core.common.service.persistence.AbstractAuditingEntity;

/**
 * Domain model for route seat.
 * Persistence concerns (JPA annotations, table/column mapping) live in infrastructure layer:
 * {@code infrastructure.persistence.jpa.route.entity.RouteSeatEntity}.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public class TripSeat extends AbstractAuditingEntity {
    private String id;
    private String tripId;
    private String seatNo;
    private SeatStatus status;
    private String creator;
    private String seatTemplateId;
}

