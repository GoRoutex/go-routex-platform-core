package platform.merchant.service.interfaces.model.ticket.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import platform.core.common.service.domain.ticket.TicketStatus;
import platform.core.common.service.api.BaseResponse;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class CreateTicketResponse extends BaseResponse<List<CreateTicketResponse.CreateTicketResponseData>> {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    public static class CreateTicketResponseData {
        private String ticketId;
        private String ticketCode;
        private String bookingSeatId;
        private TicketStatus status;
    }
}
