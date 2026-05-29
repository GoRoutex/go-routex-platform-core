package platform.merchant.service.application.command.review;

import lombok.Builder;
import platform.core.common.service.common.RequestContext;
import platform.merchant.service.domain.review.ReviewType;

@Builder
public record CreateMerchantReviewCommand(
        RequestContext context,
        String merchantId,
        ReviewType reviewType,
        String bookingId,
        String tripId,
        String routeCode,
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
        String reviewedAt,
        String creator
) {
}
