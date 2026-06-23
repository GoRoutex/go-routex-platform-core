package platform.payment.service.application.command.payment;

import lombok.Builder;
import platform.core.common.service.common.RequestContext;
import platform.core.common.service.domain.payment.PaymentMethod;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record GetPaymentUrlCommand(
        RequestContext context,
        BigDecimal amount,
        String referenceNo,
        String bankCode,
        String clientIp,
        String bookingCode,
        PaymentMethod method
) {
}
