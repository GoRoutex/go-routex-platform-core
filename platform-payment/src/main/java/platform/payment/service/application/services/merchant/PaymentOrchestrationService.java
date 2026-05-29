package platform.payment.service.application.services.merchant;

import platform.payment.service.application.command.payment.GetPaymentUrlCommand;
import platform.payment.service.application.command.payment.GetPaymentUrlResult;
import platform.payment.service.application.command.payment.PollingPaymentStatusCommand;
import platform.payment.service.application.command.payment.PollingPaymentStatusResult;

public interface PaymentOrchestrationService {

    GetPaymentUrlResult getPaymentUrl(GetPaymentUrlCommand command);

    PollingPaymentStatusResult pollingStatus(PollingPaymentStatusCommand command);
}
