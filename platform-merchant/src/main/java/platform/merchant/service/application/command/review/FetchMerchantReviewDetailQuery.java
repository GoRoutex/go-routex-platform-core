package platform.merchant.service.application.command.review;

import lombok.Builder;
import platform.core.common.service.common.RequestContext;

@Builder
public record FetchMerchantReviewDetailQuery(
        RequestContext context,
        String merchantId,
        String reviewId
) {
}
