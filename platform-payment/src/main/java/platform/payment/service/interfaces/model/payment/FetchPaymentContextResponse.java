package platform.payment.service.interfaces.model.payment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import platform.core.common.service.api.BaseResponse;

import java.time.OffsetDateTime;

@Getter
@Setter
@SuperBuilder
public class FetchPaymentContextResponse extends BaseResponse<FetchPaymentContextResponse.Data> {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    public static class Data {
        private String paymentId;
        private String bookingCode;
        private String paymentStatus;
        private OffsetDateTime paidAt;
    }
}
