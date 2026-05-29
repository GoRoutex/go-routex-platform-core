package platform.merchant.service.interfaces.model.review;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import platform.merchant.service.domain.review.ReviewType;
import platform.core.common.service.api.BaseResponse;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class FetchMerchantReviewsResponse extends BaseResponse<FetchMerchantReviewsResponse.FetchMerchantReviewsPage> {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    public static class FetchMerchantReviewsPage {
        private List<FetchMerchantReviewResponseData> items;
        private Summary summary;
        private Pagination pagination;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    public static class FetchMerchantReviewResponseData {
        private String id;
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

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    public static class Summary {
        private long totalReviews;
        private Double averageOverallRating;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    public static class Pagination {
        private int pageNumber;
        private int pageSize;
        private long totalElements;
        private int totalPages;
    }
}
