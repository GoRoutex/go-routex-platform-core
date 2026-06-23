package platform.payment.service.application.command.payment;


import lombok.Builder;
import platform.core.common.service.common.RequestContext;
import platform.core.common.service.domain.payment.PaymentMethod;

@Builder
public record PollingPaymentStatusCommand(
        RequestContext context,
        String bookingCode,
        PaymentMethod method
) {
}
