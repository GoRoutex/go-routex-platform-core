package platform.payment.service.application.services;

import platform.core.common.service.common.RequestContext;
import platform.core.common.service.domain.payment.model.PaymentAggregate;

public interface PaymentContextQueryService {

    PaymentAggregate getPaymentContext(String bookingCode, RequestContext context);
}
