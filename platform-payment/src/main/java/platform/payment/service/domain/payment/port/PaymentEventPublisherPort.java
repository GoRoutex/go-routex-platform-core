package platform.payment.service.domain.payment.port;

import platform.core.common.service.common.RequestContext;
import platform.core.common.service.domain.payment.model.PaymentAggregate;

public interface PaymentEventPublisherPort {
    void publishPaymentSucceeded(RequestContext context, PaymentAggregate paymentAggregate);

    void publishPaymentFailed(RequestContext context, PaymentAggregate paymentAggregate, String reason);
}
