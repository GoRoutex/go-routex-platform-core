package platform.merchant.service.application.command.ticket;

import lombok.Builder;
import platform.core.common.service.common.RequestContext;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Builder
public record CreateTicketCommand(
        RequestContext context,
        String bookingId,
        String bookingSeatId,
        String merchantId,
        String tripId,
        String vehicleId,
        String seatNumber,
        String customerName,
        String customerPhone,
        String customerEmail,
        BigDecimal price,
        String promotionCode,
        OffsetDateTime issuedAt,
        String creator,
        String pickupType,
        String pickupStopId,
        String pickupAddress,
        String dropoffType,
        String dropoffStopId,
        String dropoffAddress
) {}
