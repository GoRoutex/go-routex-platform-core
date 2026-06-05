package platform.payment.service.interfaces.model.payment;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import platform.core.common.service.api.BaseResponse;
import platform.core.common.service.domain.booking.PaymentStatus;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class PollingPaymentStatus extends BaseResponse<PollingPaymentStatus.PollingPaymentStatusData> {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    public static class PollingPaymentStatusData {
        private String bookingCode;
        private PaymentStatus status;
        private BigDecimal amount;
    }
}
