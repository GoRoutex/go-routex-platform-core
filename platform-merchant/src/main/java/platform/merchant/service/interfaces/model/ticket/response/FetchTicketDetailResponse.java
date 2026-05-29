package platform.merchant.service.interfaces.model.ticket.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import platform.core.common.service.api.BaseResponse;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class FetchTicketDetailResponse extends BaseResponse<TicketResponse> {
}
