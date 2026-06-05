package platform.payment.service.application.services.merchant.impl;

import org.springframework.stereotype.Service;
import platform.core.common.service.domain.payment.PaymentMethod;
import platform.payment.service.application.command.payment.GetPaymentUrlCommand;
import platform.payment.service.application.command.payment.GetPaymentUrlResult;
import platform.payment.service.application.services.merchant.PaymentMerchantService;

@Service
public class VisaMerchantServiceImpl implements PaymentMerchantService {
    @Override
    public PaymentMethod getPaymentMethod() {
        return PaymentMethod.VISA;
    }

    @Override
    public GetPaymentUrlResult getPaymentUrl(GetPaymentUrlCommand command) {
        return null;
    }
}
