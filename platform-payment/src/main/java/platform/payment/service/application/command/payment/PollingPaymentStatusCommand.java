package platform.payment.service.application.command.payment;


import lombok.Builder;
import platform.core.common.service.common.RequestContext;

@Builder
public record PollingPaymentStatusCommand(
        RequestContext context,
        String bookingCode
) {
}
