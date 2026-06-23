package platform.payment.service.infrastructure.persistence.jpa.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import platform.payment.service.infrastructure.persistence.jpa.payment.entity.PaymentBookingEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentBookingEntityRepository extends JpaRepository<PaymentBookingEntity, String> {

    List<PaymentBookingEntity> findByPaymentId(String paymentId);

    Optional<PaymentBookingEntity> findFirstByBookingCodeOrderByCreatedAtDesc(String bookingCode);

    List<PaymentBookingEntity> findByBookingCodeOrderByCreatedAtDesc(String bookingCode);

    boolean existsByPaymentIdAndBookingCode(String paymentId, String bookingCode);
}
