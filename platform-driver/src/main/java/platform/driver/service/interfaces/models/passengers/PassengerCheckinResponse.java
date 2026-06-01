package platform.driver.service.interfaces.models.passengers;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import platform.core.common.service.api.BaseResponse;
import platform.core.common.service.domain.ticket.TicketStatus;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class PassengerCheckinResponse extends BaseResponse<PassengerCheckinResponse.PassengerCheckinResponseData> {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    public static class PassengerCheckinResponseData {
        private String ticketCode;
        private String customerName;
        private String seatNumber;
        private String tripId;
        private TicketStatus status;
        private OffsetDateTime checkedInAt;
        private OffsetDateTime boardedAt;
    }

}
