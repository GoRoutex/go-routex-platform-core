package platform.merchant.service.interfaces.model.review;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import platform.core.common.service.api.BaseResponse;
import platform.merchant.service.domain.review.ReviewType;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class CreateMerchantReviewResponse extends BaseResponse<CreateMerchantReviewResponse.CreateMerchantReviewResponseData> {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    public static class CreateMerchantReviewResponseData {
        private String id;
        private String merchantId;
        private ReviewType reviewType;
        private String bookingId;
        private String tripId;
        private String tripCode;
        private String driverId;
        private String vehicleId;
        private String customerId;
        private String customerName;
        private Integer overallRating;
        private Integer driverRating;
        private Integer vehicleRating;
        private Integer punctualityRating;
        private Integer tripExperienceRating;
        private Integer safetyRating;
        private Integer merchantServiceRating;
        private Integer staffSupportRating;
        private Integer valueForMoneyRating;
        private String comment;
        private String reviewedAt;
    }
}
