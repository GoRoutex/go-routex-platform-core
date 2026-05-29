package platform.core.common.service.api;

import platform.core.common.service.common.RequestContext;
import platform.core.common.service.domain.payment.model.PaymentAggregate;

public interface InternalPaymentContextService {
    PaymentAggregate getPaymentContext(String bookingCode, RequestContext context);
}
