package platform.payment.service.application.services.merchant.factory;

import org.springframework.stereotype.Service;
import platform.core.common.service.domain.payment.PaymentMethod;
import platform.payment.service.application.services.merchant.PaymentMerchantService;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class PaymentMerchantServiceFactory {

    private final Map<PaymentMethod, PaymentMerchantService> serviceMap;

    public PaymentMerchantServiceFactory(List<PaymentMerchantService> services) {
        this.serviceMap = services.stream()
                .collect(Collectors.toMap(
                        PaymentMerchantService::getPaymentMethod,
                        Function.identity()
                ));
    }

    public PaymentMerchantService getService(PaymentMethod method) {
        PaymentMerchantService service = serviceMap.get(method);
        if(service == null) {
            throw new IllegalArgumentException("Unsupported payment method: " + method);
        }
        return service;
    }
}
