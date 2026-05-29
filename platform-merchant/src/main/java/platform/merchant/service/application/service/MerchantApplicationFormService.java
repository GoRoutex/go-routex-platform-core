package platform.merchant.service.application.service;

import platform.merchant.service.application.command.merchant.AcceptMerchantApplicationCommand;
import platform.merchant.service.application.command.merchant.AcceptMerchantApplicationResult;
import platform.merchant.service.application.command.merchant.RejectMerchantApplicationCommand;
import platform.merchant.service.application.command.merchant.RejectMerchantApplicationResult;
import platform.merchant.service.application.command.merchant.SubmitMerchantApplicationCommand;
import platform.merchant.service.application.command.merchant.SubmitMerchantApplicationResult;

public interface MerchantApplicationFormService {

    SubmitMerchantApplicationResult submit(SubmitMerchantApplicationCommand command);

    AcceptMerchantApplicationResult accept(AcceptMerchantApplicationCommand command);

    RejectMerchantApplicationResult reject(RejectMerchantApplicationCommand command);
}
