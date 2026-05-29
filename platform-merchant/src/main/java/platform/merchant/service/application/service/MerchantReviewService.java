package platform.merchant.service.application.service;

import platform.merchant.service.application.command.review.CreateMerchantReviewCommand;
import platform.merchant.service.application.command.review.CreateMerchantReviewResult;
import platform.merchant.service.application.command.review.FetchMerchantReviewDetailQuery;
import platform.merchant.service.application.command.review.FetchMerchantReviewDetailResult;
import platform.merchant.service.application.command.review.FetchMerchantReviewsQuery;
import platform.merchant.service.application.command.review.FetchMerchantReviewsResult;

public interface MerchantReviewService {

    CreateMerchantReviewResult createReview(CreateMerchantReviewCommand command);

    FetchMerchantReviewsResult fetchReviews(FetchMerchantReviewsQuery query);

    FetchMerchantReviewDetailResult fetchReviewDetail(FetchMerchantReviewDetailQuery query);
}
