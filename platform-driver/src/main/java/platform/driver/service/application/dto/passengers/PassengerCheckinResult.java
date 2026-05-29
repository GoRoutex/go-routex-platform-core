package platform.driver.service.application.dto.passengers;


import lombok.Builder;
import platform.core.common.service.domain.booking.BookingSeatStatus;

import java.time.OffsetDateTime;

@Builder
public record PassengerCheckinResult(
    String ticketCode,
    String customerName,
    String seatNumber,
    String routeCode,
    BookingSeatStatus status,
    OffsetDateTime checkedInAt
) {
}
