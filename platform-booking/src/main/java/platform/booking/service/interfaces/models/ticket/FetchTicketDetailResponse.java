package platform.booking.service.interfaces.models.ticket;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import platform.core.common.service.domain.ticket.TicketStatus;
import platform.core.common.service.api.BaseResponse;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class FetchTicketDetailResponse extends BaseResponse<FetchTicketDetailResponse.FetchTicketDetailResponseData> {

    @Getter
    @Setter
    @NoArgsConstructor
    @SuperBuilder
    public static class FetchTicketDetailResponseData {
        private String id;
        private String ticketCode;
        private String bookingId;
        private String bookingSeatId;
        private String tripId;
        private String seatNumber;
        private String customerName;
        private String customerPhone;
        private BigDecimal price;
        private TicketStatus status;
        private OffsetDateTime issuedAt;
        private OffsetDateTime checkedInAt;
        private OffsetDateTime boardedAt;
        private OffsetDateTime cancelledAt;
        private String checkedInBy;
        private String boardedBy;
        private String cancelledBy;
    }
}
