package platform.payment.service.infrastructure.persistence.jpa.merchant.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import platform.payment.service.domain.merchant.MerchantSessionStatus;
import platform.payment.service.infrastructure.persistence.jpa.merchant.entity.MerchantSessionEntity;

import java.util.Optional;

@Repository
public interface MerchantSessionEntityRepository extends JpaRepository<MerchantSessionEntity, String> {
    @Query("SELECT m FROM MerchantSessionEntity m WHERE m.paymentId = :paymentId " +
            "AND m.status = :status ORDER BY m.createdAt DESC")
    Optional<MerchantSessionEntity> findLatestByPaymentIdAndStatus(@Param("paymentId") String paymentId,
                                           @Param("status") MerchantSessionStatus status);

    @Query("SELECT COUNT(ms) FROM MerchantSessionEntity ms WHERE ms.paymentId = :id")
    int countByPaymentId(String id);
}
