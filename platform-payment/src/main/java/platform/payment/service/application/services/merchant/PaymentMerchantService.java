package platform.payment.service.application.services.merchant;

import platform.payment.service.application.command.payment.GetPaymentUrlCommand;
import platform.payment.service.application.command.payment.GetPaymentUrlResult;
import platform.core.common.service.domain.payment.PaymentMethod;

public interface PaymentMerchantService {

    PaymentMethod getPaymentMethod();

    GetPaymentUrlResult getPaymentUrl(GetPaymentUrlCommand command);
}
