package platform.payment.service.interfaces.model.payment;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import platform.core.common.service.api.BaseResponse;
import platform.core.common.service.domain.booking.PaymentStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class CheckoutResponse extends BaseResponse<CheckoutResponse.CheckoutResponseData> {


    @Getter
    @Setter
    @NoArgsConstructor
    @SuperBuilder
    public static class CheckoutResponseData {
        private String paymentId;
        private PaymentStatus status;
        private BigDecimal amount;
        private String currency;
        private OffsetDateTime paidAt;
    }
}
