package platform.merchant.service.infrastructure.persistence.jpa.trip.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import platform.core.common.service.domain.trip.TripStatus;
import platform.core.common.service.persistence.AbstractAuditingEntity;

import java.time.OffsetDateTime;

@Table(name = "TRIP")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class TripEntity extends AbstractAuditingEntity {
    @Id
    private String id;

    @Column(name = "ROUTE_ID", nullable = false, unique = true)
    private String routeId;

    @Column(name = "MERCHANT_ID", nullable = false)
    private String merchantId;

    @Column(name = "CREATOR")
    private String creator;

    @Column(name = "TRIP_CODE", nullable = false, unique = true)
    private String tripCode;

    @Column(name = "DEPARTURE_TIME")
    private OffsetDateTime departureTime;

    @Column(name = "RAW_DEPARTURE_TIME")
    private String rawDepartureTime;

    @Column(name = "RAW_DEPARTURE_DATE")
    private String rawDepartureDate;

    @Column(name = "STATUS")
    @Enumerated(EnumType.STRING)
    private TripStatus status;

}
