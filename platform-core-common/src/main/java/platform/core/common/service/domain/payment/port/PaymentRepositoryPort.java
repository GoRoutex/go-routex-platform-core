package platform.core.common.service.domain.payment.port;

import platform.core.common.service.domain.booking.PaymentStatus;
import platform.core.common.service.domain.payment.PaymentMethod;
import platform.core.common.service.domain.payment.model.PaymentAggregate;

import java.util.Optional;

public interface PaymentRepositoryPort {
    Optional<PaymentAggregate> findById(String paymentId);

    Optional<PaymentAggregate> findByBookingCodeAndMethodAndStatus(
            String bookingCode,
            PaymentMethod method,
            PaymentStatus status
    );

    PaymentAggregate save(PaymentAggregate paymentAggregate);

    Optional<PaymentAggregate> findByTxnRef(String txnRef);

    Optional<PaymentAggregate> findByBookingCode(String bookingCode);
}
