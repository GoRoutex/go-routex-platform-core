package platform.payment.service.interfaces.model.payment;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import platform.core.common.service.api.BaseRequest;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class CheckoutRequest extends BaseRequest {

    @Valid
    @NotNull
    private CheckoutRequestData data;


    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    public static class CheckoutRequestData {
        private String paymentId;
        private String bookingId;
        private String token;
    }
}
