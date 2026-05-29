package platform.merchant.service.infrastructure.persistence.jpa.review.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import platform.merchant.service.domain.review.ReviewType;
import platform.merchant.service.infrastructure.persistence.jpa.review.entity.MerchantReviewEntity;

@Repository
public interface MerchantReviewEntityRepository extends JpaRepository<MerchantReviewEntity, String> {

    java.util.Optional<MerchantReviewEntity> findByIdAndMerchantId(String id, String merchantId);

    Page<MerchantReviewEntity> findByMerchantIdOrderByReviewedAtDesc(String merchantId, Pageable pageable);

    long countByMerchantId(String merchantId);

    boolean existsByMerchantIdAndBookingIdAndCustomerIdAndReviewType(
            String merchantId,
            String bookingId,
            String customerId,
            ReviewType reviewType
    );

    @Query("select avg(r.overallRating) from MerchantReviewEntity r where r.merchantId = :merchantId")
    Double findAverageOverallRatingByMerchantId(@Param("merchantId") String merchantId);
}
