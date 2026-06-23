package platform.core.common.service.domain.booking.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import platform.core.common.service.persistence.AbstractAuditingEntity;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class BookingLeg extends AbstractAuditingEntity {
    private String id;
    private String bookingId;
    private String tripId;
    private String vehicleId;
    private String pickupType;
    private String pickupStopId;
    private String pickupAddress;
    private String dropOffType;
    private String dropOffStopId;
    private String dropOffAddress;
}
