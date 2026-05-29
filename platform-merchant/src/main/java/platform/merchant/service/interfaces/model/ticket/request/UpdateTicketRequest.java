package platform.merchant.service.interfaces.model.ticket.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import platform.core.common.service.api.BaseRequest;
import platform.core.common.service.domain.ticket.TicketStatus;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class UpdateTicketRequest extends BaseRequest {
    private UpdateTicketData data;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    public static class UpdateTicketData {
        @NotBlank
        private String ticketId;
        private String customerName;
        private String customerPhone;
        private String customerEmail;
        private TicketStatus status;
    }
}
