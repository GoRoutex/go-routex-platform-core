package platform.payment.service.interfaces.internal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import platform.core.common.service.api.InternalPaymentContextService;
import platform.core.common.service.common.RequestContext;
import platform.core.common.service.domain.payment.model.PaymentAggregate;
import platform.payment.service.application.services.PaymentContextQueryService;
import vn.com.go.routex.identity.security.log.SystemLog;

@Service
@RequiredArgsConstructor
public class InternalPaymentContextServiceImpl implements InternalPaymentContextService {

    private final PaymentContextQueryService paymentContextQueryService;
    private final SystemLog sLog = SystemLog.getLogger(this.getClass());

    @Override
    public PaymentAggregate getPaymentContext(String bookingCode, RequestContext context) {
        sLog.info("[INTERNAL] Received fetchPaymentContext request for bookingCode: {}", bookingCode);

        return paymentContextQueryService.getPaymentContext(bookingCode, context);
    }
}
