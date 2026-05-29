package platform.merchant.service.infrastructure.persistence.adapter.review;

import org.springframework.stereotype.Component;
import platform.merchant.service.domain.review.model.MerchantReview;
import platform.merchant.service.infrastructure.persistence.jpa.review.entity.MerchantReviewEntity;

@Component
public class MerchantReviewPersistenceMapper {

    public MerchantReviewEntity toEntity(MerchantReview review) {
        return MerchantReviewEntity.builder()
                .id(review.getId())
                .merchantId(review.getMerchantId())
                .reviewType(review.getReviewType())
                .bookingId(review.getBookingId())
                .tripId(review.getTripId())
                .tripCode(review.getTripCode())
                .driverId(review.getDriverId())
                .vehicleId(review.getVehicleId())
                .customerId(review.getCustomerId())
                .customerName(review.getCustomerName())
                .overallRating(review.getOverallRating())
                .driverRating(review.getDriverRating())
                .vehicleRating(review.getVehicleRating())
                .punctualityRating(review.getPunctualityRating())
                .tripExperienceRating(review.getTripExperienceRating())
                .safetyRating(review.getSafetyRating())
                .merchantServiceRating(review.getMerchantServiceRating())
                .staffSupportRating(review.getStaffSupportRating())
                .valueForMoneyRating(review.getValueForMoneyRating())
                .comment(review.getComment())
                .reviewedAt(review.getReviewedAt())
                .createdAt(review.getCreatedAt())
                .createdBy(review.getCreatedBy())
                .updatedAt(review.getUpdatedAt())
                .updatedBy(review.getUpdatedBy())
                .build();
    }

    public MerchantReview toDomain(MerchantReviewEntity entity) {
        return MerchantReview.builder()
                .id(entity.getId())
                .merchantId(entity.getMerchantId())
                .reviewType(entity.getReviewType())
                .bookingId(entity.getBookingId())
                .tripId(entity.getTripId())
                .tripCode(entity.getTripCode())
                .driverId(entity.getDriverId())
                .vehicleId(entity.getVehicleId())
                .customerId(entity.getCustomerId())
                .customerName(entity.getCustomerName())
                .overallRating(entity.getOverallRating())
                .driverRating(entity.getDriverRating())
                .vehicleRating(entity.getVehicleRating())
                .punctualityRating(entity.getPunctualityRating())
                .tripExperienceRating(entity.getTripExperienceRating())
                .safetyRating(entity.getSafetyRating())
                .merchantServiceRating(entity.getMerchantServiceRating())
                .staffSupportRating(entity.getStaffSupportRating())
                .valueForMoneyRating(entity.getValueForMoneyRating())
                .comment(entity.getComment())
                .reviewedAt(entity.getReviewedAt())
                .createdAt(entity.getCreatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedAt(entity.getUpdatedAt())
                .updatedBy(entity.getUpdatedBy())
                .build();
    }
}
