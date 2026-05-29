package platform.merchant.service.application.command.email;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Builder
public record TicketEmailCommand(
    String toEmail,
    String customerName,
    String ticketCode,
    String seatNumber,
    BigDecimal price,
    String routeName,
    OffsetDateTime departureTime,
    String startPoint,
    String endPoint,
    String driverName,
    String driverPhone,
    String vehiclePlate,
    String vehicleType
) {}
