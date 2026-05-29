package platform.merchant.service.application.command.review;

import lombok.Builder;
import platform.merchant.service.domain.review.ReviewType;

@Builder
public record FetchMerchantReviewDetailResult(
        String id,
        String merchantId,
        ReviewType reviewType,
        String bookingId,
        String tripId,
        String tripCode,
        String driverId,
        String vehicleId,
        String customerId,
        String customerName,
        Integer overallRating,
        Integer driverRating,
        Integer vehicleRating,
        Integer punctualityRating,
        Integer tripExperienceRating,
        Integer safetyRating,
        Integer merchantServiceRating,
        Integer staffSupportRating,
        Integer valueForMoneyRating,
        String comment,
        String reviewedAt
) {
}
