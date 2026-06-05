package platform.payment.service.application.services.merchant;

import platform.core.common.service.domain.payment.PaymentMethod;
import platform.payment.service.application.command.payment.GetPaymentUrlCommand;
import platform.payment.service.application.command.payment.GetPaymentUrlResult;

public interface PaymentMerchantService {

    PaymentMethod getPaymentMethod();

    GetPaymentUrlResult getPaymentUrl(GetPaymentUrlCommand command);
}
