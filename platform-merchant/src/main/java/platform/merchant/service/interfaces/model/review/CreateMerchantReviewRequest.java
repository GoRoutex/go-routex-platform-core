package platform.merchant.service.interfaces.model.review;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import platform.merchant.service.domain.review.ReviewType;
import platform.core.common.service.api.BaseRequest;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class CreateMerchantReviewRequest extends BaseRequest {

    @Valid
    @NotNull
    private CreateMerchantReviewRequestData data;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    public static class CreateMerchantReviewRequestData {
        @NotBlank
        private String merchantId;
        @NotNull
        private ReviewType reviewType;
        private String bookingId;
        private String tripId;
        private String routeCode;
        private String driverId;
        private String vehicleId;
        private String customerId;
        private String customerName;
        @NotNull
        @Min(1)
        @Max(5)
        private Integer overallRating;
        @Min(1)
        @Max(5)
        private Integer driverRating;
        @Min(1)
        @Max(5)
        private Integer vehicleRating;
        @Min(1)
        @Max(5)
        private Integer punctualityRating;
        @Min(1)
        @Max(5)
        private Integer tripExperienceRating;
        @Min(1)
        @Max(5)
        private Integer safetyRating;
        @Min(1)
        @Max(5)
        private Integer merchantServiceRating;
        @Min(1)
        @Max(5)
        private Integer staffSupportRating;
        @Min(1)
        @Max(5)
        private Integer valueForMoneyRating;
        private String comment;
        private String reviewedAt;
        private String creator;
    }
}
