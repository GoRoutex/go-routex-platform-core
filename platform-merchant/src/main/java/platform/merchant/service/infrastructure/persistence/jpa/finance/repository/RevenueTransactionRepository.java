package platform.merchant.service.infrastructure.persistence.jpa.finance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import platform.merchant.service.infrastructure.persistence.jpa.finance.entity.RevenueTransactionEntity;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Repository
public interface RevenueTransactionRepository extends JpaRepository<RevenueTransactionEntity, String> {

    List<RevenueTransactionEntity> findAllByMerchantId(String merchantId);

    @Query("SELECT SUM(r.merchantAmount) FROM RevenueTransactionEntity r WHERE r.merchantId = :merchantId AND r.transactionDate BETWEEN :startDate AND :endDate")
    BigDecimal sumMerchantAmountByMerchantIdAndDateBetween(@Param("merchantId") String merchantId, 
                                                           @Param("startDate") OffsetDateTime startDate, 
                                                           @Param("endDate") OffsetDateTime endDate);

    @Query("SELECT SUM(r.systemAmount) FROM RevenueTransactionEntity r WHERE r.transactionDate BETWEEN :startDate AND :endDate")
    BigDecimal sumSystemAmountByDateBetween(@Param("startDate") OffsetDateTime startDate, 
                                            @Param("endDate") OffsetDateTime endDate);
}
