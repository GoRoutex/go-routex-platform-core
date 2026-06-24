package platform.core.common.service.domain.ticket.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import platform.core.common.service.domain.ticket.TicketStatus;
import platform.core.common.service.persistence.AbstractAuditingEntity;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Ticket extends AbstractAuditingEntity {
    private String id;
    private String ticketCode;
    private String bookingId;
    private String bookingSeatId;
    private String tripId;
    private String vehicleId;
    private String seatNumber;
    private String customerId;
    private String customerName;
    private String customerPhone;
    private String customerEmail;
    private BigDecimal price;
    private String merchantId;
    private TicketStatus status;
    private OffsetDateTime issuedAt;
    private OffsetDateTime checkedInAt;
    private OffsetDateTime boardedAt;
    private OffsetDateTime cancelledAt;
    private String checkedInBy;
    private String boardedBy;
    private String cancelledBy;
    private String pickupType;
    private String pickupStopId;
    private String pickupAddress;
    private String dropOffType;
    private String dropOffStopId;
    private String dropOffAddress;
}
