package platform.payment.service.infrastructure.persistence.jpa.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import platform.core.common.service.domain.booking.PaymentStatus;
import platform.core.common.service.domain.payment.PaymentMethod;
import platform.payment.service.infrastructure.persistence.jpa.payment.entity.PaymentEntity;

import java.util.Optional;

@Repository
public interface PaymentEntityRepository extends JpaRepository<PaymentEntity, String> {
    Optional<PaymentEntity> findByBookingCodeAndMethodAndStatus(String bookingCode, PaymentMethod method, PaymentStatus status);

    Optional<PaymentEntity> findByTxnRef(String txnRef);

    Optional<PaymentEntity> findByBookingCode(String bookingCode);
}
