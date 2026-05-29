package platform.merchant.service.domain.assignment.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import platform.core.common.service.persistence.AbstractAuditingEntity;
import platform.merchant.service.domain.assignment.TripAssignmentStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class TripAssignmentRecord extends AbstractAuditingEntity {
    private String id;
    private String tripId;
    private String creator;
    private String merchantId;
    private String vehicleId;
    private String driverId;
    private BigDecimal ticketPrice;
    private OffsetDateTime assignedAt;
    private OffsetDateTime unAssignedAt;
    private TripAssignmentStatus status;

    public static TripAssignmentRecord assign(
            String id,
            String tripId,
            String creator,
            String merchantId,
            String vehicleId,
            String driverId,
            BigDecimal ticketPrice,
            OffsetDateTime assignedAt
    ) {
        return TripAssignmentRecord.builder()
                .id(id)
                .tripId(tripId)
                .creator(creator)
                .merchantId(merchantId)
                .driverId(driverId)
                .vehicleId(vehicleId)
                .assignedAt(assignedAt)
                .ticketPrice(ticketPrice)
                .status(TripAssignmentStatus.PENDING_ASSIGNMENT)
                .build();
    }

    public void complete(String actor, OffsetDateTime at) {
        this.status = TripAssignmentStatus.COMPLETED;
        this.setUpdatedAt(at);
        this.setUpdatedBy(actor);
    }

    public void cancel(String actor, OffsetDateTime at) {
        this.status = TripAssignmentStatus.CANCELED;
        this.unAssignedAt = at;
        this.setUpdatedAt(at);
        this.setUpdatedBy(actor);
    }
}
