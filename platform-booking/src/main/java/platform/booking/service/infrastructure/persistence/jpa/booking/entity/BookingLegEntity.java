package platform.booking.service.infrastructure.persistence.jpa.booking.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import platform.core.common.service.persistence.AbstractAuditingEntity;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Entity(name = "Booking_BookingLegEntity")
@Table(name = "BOOKING_LEG")
public class BookingLegEntity extends AbstractAuditingEntity {
    @Id
    private String id;

    @Column(name = "BOOKING_ID", nullable = false)
    private String bookingId;

    @Column(name = "TRIP_ID", nullable = false)
    private String tripId;

    @Column(name = "VEHICLE_ID")
    private String vehicleId;

    @Column(name = "PICKUP_TYPE")
    private String pickupType;

    @Column(name = "PICKUP_STOP_ID")
    private String pickupStopId;

    @Column(name = "PICKUP_ADDRESS")
    private String pickupAddress;

    @Column(name = "DROP_OFF_TYPE")
    private String dropOffType;

    @Column(name = "DROP_OFF_STOP_ID")
    private String dropOffStopId;

    @Column(name = "DROP_OFF_ADDRESS")
    private String dropOffAddress;
}
