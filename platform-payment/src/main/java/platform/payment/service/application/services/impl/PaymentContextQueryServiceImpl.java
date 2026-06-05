package platform.payment.service.application.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import platform.core.common.service.common.RequestContext;
import platform.core.common.service.domain.payment.model.PaymentAggregate;
import platform.core.common.service.persistence.exception.BusinessException;
import platform.core.common.service.persistence.utils.ExceptionUtils;
import platform.payment.service.application.services.PaymentContextQueryService;
import platform.payment.service.domain.payment.port.PaymentRepositoryPort;

import static platform.core.common.service.persistence.constant.ErrorConstant.PAYMENT_NOT_FOUND;
import static platform.core.common.service.persistence.constant.ErrorConstant.RECORD_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class PaymentContextQueryServiceImpl implements PaymentContextQueryService {

    private final PaymentRepositoryPort paymentRepositoryPort;

    @Override
    public PaymentAggregate getPaymentContext(String bookingCode, RequestContext context) {
        return paymentRepositoryPort.findByBookingCode(bookingCode)
                .orElseThrow(() -> new BusinessException(
                        context.requestId(),
                        context.requestDateTime(),
                        context.channel(),
                        ExceptionUtils.buildResultResponse(RECORD_NOT_FOUND, String.format(PAYMENT_NOT_FOUND, bookingCode))
                ));
    }
}
