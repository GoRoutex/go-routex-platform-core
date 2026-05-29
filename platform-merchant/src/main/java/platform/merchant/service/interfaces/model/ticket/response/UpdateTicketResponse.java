package platform.merchant.service.interfaces.model.ticket.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import platform.core.common.service.api.BaseResponse;
import platform.core.common.service.domain.ticket.TicketStatus;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class UpdateTicketResponse extends BaseResponse<UpdateTicketResponse.UpdateTicketResponseData> {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    public static class UpdateTicketResponseData {
        private String ticketId;
        private TicketStatus status;
    }
}
