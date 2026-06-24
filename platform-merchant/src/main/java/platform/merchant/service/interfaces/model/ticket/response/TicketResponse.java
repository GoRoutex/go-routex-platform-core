package platform.merchant.service.interfaces.model.ticket.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import platform.core.common.service.domain.ticket.TicketStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TicketResponse {
    private String id;
    private String ticketCode;
    private String bookingId;
    private String merchantId;
    private String tripId;
    private String vehicleId;
    private String seatNumber;
    private String customerId;
    private String customerName;
    private String customerPhone;
    private String customerEmail;
    private BigDecimal price;
    private TicketStatus status;
    private OffsetDateTime issuedAt;
    private OffsetDateTime checkedInAt;
    private OffsetDateTime boardedAt;
    private OffsetDateTime cancelledAt;
    private String pickupType;
    private String pickupStopId;
    private String pickupAddress;
    private String dropOffType;
    private String dropOffStopId;
    private String dropOffAddress;
}
