package platform.merchant.service.domain.review.port;

import platform.core.common.service.application.command.common.PagedResult;
import platform.merchant.service.domain.review.model.MerchantReview;

import java.util.Optional;

public interface MerchantReviewRepositoryPort {

    MerchantReview save(MerchantReview merchantReview);

    Optional<MerchantReview> findById(String reviewId, String merchantId);

    PagedResult<MerchantReview> fetchByMerchantId(String merchantId, int pageNumber, int pageSize);

    long countByMerchantId(String merchantId);

    Double findAverageOverallRatingByMerchantId(String merchantId);

    boolean existsTripReview(String merchantId, String bookingId, String customerId);
}
