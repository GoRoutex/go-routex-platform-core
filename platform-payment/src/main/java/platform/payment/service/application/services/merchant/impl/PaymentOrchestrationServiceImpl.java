package platform.payment.service.application.services.merchant.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import platform.core.common.service.domain.booking.PaymentStatus;
import platform.core.common.service.domain.payment.model.PaymentAggregate;
import platform.core.common.service.domain.payment.port.PaymentRepositoryPort;
import platform.core.common.service.persistence.exception.BusinessException;
import platform.core.common.service.persistence.utils.ExceptionUtils;
import platform.payment.service.application.command.payment.GetPaymentUrlCommand;
import platform.payment.service.application.command.payment.GetPaymentUrlResult;
import platform.payment.service.application.command.payment.PollingPaymentStatusCommand;
import platform.payment.service.application.command.payment.PollingPaymentStatusResult;
import platform.payment.service.application.services.merchant.PaymentMerchantService;
import platform.payment.service.application.services.merchant.PaymentOrchestrationService;
import platform.payment.service.application.services.merchant.factory.PaymentMerchantServiceFactory;

import static platform.core.common.service.persistence.constant.ErrorConstant.PAYMENT_NOT_FOUND;
import static platform.core.common.service.persistence.constant.ErrorConstant.RECORD_NOT_FOUND;


@Service
@RequiredArgsConstructor
public class PaymentOrchestrationServiceImpl implements PaymentOrchestrationService {

    private final PaymentMerchantServiceFactory factory;
    private final PaymentRepositoryPort paymentRepositoryPort;

    @Override
    public GetPaymentUrlResult getPaymentUrl(GetPaymentUrlCommand command) {
        PaymentMerchantService service = factory.getService(command.method());
        return service.getPaymentUrl(command);
    }

    @Override
    public PollingPaymentStatusResult pollingStatus(PollingPaymentStatusCommand command) {

        PaymentAggregate paymentAggregate = (command.method() == null
                ? paymentRepositoryPort.findByBookingCode(command.bookingCode())
                : paymentRepositoryPort.findByBookingCodeAndMethod(command.bookingCode(), command.method()))
                .orElseThrow(() -> new BusinessException(command.context().requestId(), command.context().requestDateTime(), command.context().channel(),
                        ExceptionUtils.buildResultResponse(RECORD_NOT_FOUND, String.format(PAYMENT_NOT_FOUND, command.bookingCode()))));

        if(PaymentStatus.FAILED.equals(paymentAggregate.getStatus())) {
            return PollingPaymentStatusResult.builder()
                    .status(PaymentStatus.FAILED)
                    .shouldStopPooling(true)
                    .build();
        }
        return PollingPaymentStatusResult.builder()
                .status(paymentAggregate.getStatus())
                .amount(paymentAggregate.getAmount())
                .bookingCode(command.bookingCode())
                .shouldStopPooling(paymentAggregate.getStatus().isFinal())
                .build();
    }
}
