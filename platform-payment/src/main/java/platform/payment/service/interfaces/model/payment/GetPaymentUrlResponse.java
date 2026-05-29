package platform.payment.service.interfaces.model.payment;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import platform.core.common.service.api.BaseResponse;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class GetPaymentUrlResponse extends BaseResponse<GetPaymentUrlResponse.GetPaymentResponseData> {

    @Getter
    @Setter
    @NoArgsConstructor
    @SuperBuilder
    public static class GetPaymentResponseData {
        private String bookingCode;
        private BigDecimal amount;
        private String paymentUrl;
        private String qrCodeUrl;
        private String deeplink;
        private OffsetDateTime expiredTime;
    }
}
