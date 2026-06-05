package platform.core.common.service.domain.booking.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import platform.core.common.service.domain.booking.BookingStatus;
import platform.core.common.service.persistence.AbstractAuditingEntity;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Booking extends AbstractAuditingEntity {
    private String id;
    private String bookingCode;
    private String tripId;
    private String merchantId;
    private String vehicleId;
    private String customerId;
    private String customerName;
    private String customerPhone;
    private String customerEmail;
    private String channel;
    private Integer seatCount;
    private BigDecimal totalAmount;
    private String currency;
    private BookingStatus status;
    private OffsetDateTime heldAt;
    private OffsetDateTime holdUntil;
    private OffsetDateTime cancelledAt;
    private String note;
    private String creator;
    private String pickupType;
    private String pickupStopId;
    private String pickupAddress;
    private String dropOffType;
    private String dropOffStopId;
    private String dropOffAddress;
}
