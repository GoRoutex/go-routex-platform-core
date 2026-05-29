package platform.payment.service.application.command.payment;

import lombok.Builder;
import platform.core.common.service.common.RequestContext;

@Builder
public record CheckoutCommand(
        RequestContext context,
        String paymentId,
        String token
) {
}
