package platform.payment.service.domain.merchant.port;

import platform.payment.service.domain.merchant.MerchantSessionStatus;
import platform.payment.service.domain.merchant.model.MerchantSessionAggregate;

import java.util.Optional;

public interface MerchantSessionRepositoryPort {

    Optional<MerchantSessionAggregate> findLatestByPaymentIdAndStatus(String paymentId, MerchantSessionStatus status);

    int countByPaymentId(String id);

    void save(MerchantSessionAggregate session);
}
