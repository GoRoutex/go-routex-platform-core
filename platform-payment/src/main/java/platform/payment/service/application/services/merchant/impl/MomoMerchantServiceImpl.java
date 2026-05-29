package platform.payment.service.application.services.merchant.impl;

import org.springframework.stereotype.Service;
import platform.payment.service.application.command.payment.GetPaymentUrlCommand;
import platform.payment.service.application.command.payment.GetPaymentUrlResult;
import platform.payment.service.application.services.merchant.PaymentMerchantService;
import platform.core.common.service.domain.payment.PaymentMethod;

@Service
public class MomoMerchantServiceImpl implements PaymentMerchantService {
    @Override
    public PaymentMethod getPaymentMethod() {
        return PaymentMethod.MOMO;
    }

    @Override
    public GetPaymentUrlResult getPaymentUrl(GetPaymentUrlCommand command) {
        return null;
    }
}
