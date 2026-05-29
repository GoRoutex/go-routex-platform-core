package platform.driver.service.interfaces.models.passengers;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import platform.core.common.service.domain.booking.BookingSeatStatus;
import platform.core.common.service.api.BaseResponse;

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
        private String routeCode;
        private BookingSeatStatus status;
        private OffsetDateTime checkedInAt;
    }

}
