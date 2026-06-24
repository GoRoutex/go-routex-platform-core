package platform.payment.service.infrastructure.persistence.adapter.payment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import platform.core.common.service.domain.booking.PaymentStatus;
import platform.core.common.service.domain.payment.PaymentMethod;
import platform.core.common.service.domain.payment.model.PaymentAggregate;
import platform.core.common.service.domain.payment.port.PaymentRepositoryPort;
import platform.payment.service.infrastructure.persistence.jpa.payment.entity.PaymentBookingEntity;
import platform.payment.service.infrastructure.persistence.jpa.payment.repository.PaymentBookingEntityRepository;
import platform.payment.service.infrastructure.persistence.jpa.payment.repository.PaymentEntityRepository;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PaymentRepositoryAdapter implements PaymentRepositoryPort {

    private final PaymentEntityRepository paymentEntityRepository;
    private final PaymentBookingEntityRepository paymentBookingEntityRepository;
    private final PaymentPersistenceMapper paymentPersistenceMapper;

    @Override
    public Optional<PaymentAggregate> findById(String paymentId) {
        return paymentEntityRepository.findById(paymentId).map(paymentPersistenceMapper::toDomain);
    }

    @Override
    public Optional<PaymentAggregate> findByBookingCodeAndMethodAndStatus(String bookingId, PaymentMethod method, PaymentStatus status) {
        return paymentEntityRepository.findByBookingCodeAndMethodAndStatus(bookingId, method, status)
                .map(paymentPersistenceMapper::toDomain);
    }

    @Override
    public PaymentAggregate save(PaymentAggregate paymentAggregate) {
        return paymentPersistenceMapper.toDomain(
                paymentEntityRepository.save(paymentPersistenceMapper.toEntity(paymentAggregate))
        );
    }

    @Override
    public Optional<PaymentAggregate> findByTxnRef(String txnRef) {
        return paymentEntityRepository.findByTxnRef(txnRef)
                .map(paymentPersistenceMapper::toDomain);
    }

    @Override
    public Optional<PaymentAggregate> findByBookingCode(String bookingCode) {
        Optional<PaymentAggregate> directPayment = paymentEntityRepository.findFirstByBookingCodeOrderByCreatedAtDesc(bookingCode)
                .map(paymentPersistenceMapper::toDomain);
        if (directPayment.isPresent()) {
            return directPayment;
        }
        return findLatestLinkedPayment(bookingCode);
    }

    @Override
    public Optional<PaymentAggregate> findByBookingCodeAndMethod(String bookingCode, PaymentMethod method) {
        Optional<PaymentAggregate> directPayment = paymentEntityRepository.findFirstByBookingCodeAndMethodOrderByCreatedAtDesc(bookingCode, method)
                .map(paymentPersistenceMapper::toDomain);
        if (directPayment.isPresent()) {
            return directPayment;
        }
        return findLatestLinkedPayment(bookingCode, method);
    }

    @Override
    public List<String> findBookingCodesByPaymentId(String paymentId) {
        return paymentBookingEntityRepository.findByPaymentId(paymentId).stream()
                .map(PaymentBookingEntity::getBookingCode)
                .toList();
    }



    private Optional<PaymentAggregate> findLatestLinkedPayment(String bookingCode) {
        return paymentBookingEntityRepository.findByBookingCodeOrderByCreatedAtDesc(bookingCode).stream()
                .map(PaymentBookingEntity::getPaymentId)
                .map(paymentEntityRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst()
                .map(paymentPersistenceMapper::toDomain);
    }

    private Optional<PaymentAggregate> findLatestLinkedPayment(String bookingCode, PaymentMethod method) {
        return paymentBookingEntityRepository.findByBookingCodeOrderByCreatedAtDesc(bookingCode).stream()
                .map(PaymentBookingEntity::getPaymentId)
                .map(paymentEntityRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(payment -> method.equals(payment.getMethod()))
                .findFirst()
                .map(paymentPersistenceMapper::toDomain);
    }
}
