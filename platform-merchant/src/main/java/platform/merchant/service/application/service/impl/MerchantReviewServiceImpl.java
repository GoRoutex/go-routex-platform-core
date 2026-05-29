package platform.merchant.service.application.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import platform.merchant.service.application.command.review.CreateMerchantReviewCommand;
import platform.merchant.service.application.command.review.CreateMerchantReviewResult;
import platform.merchant.service.application.command.review.FetchMerchantReviewDetailQuery;
import platform.merchant.service.application.command.review.FetchMerchantReviewDetailResult;
import platform.merchant.service.application.command.review.FetchMerchantReviewsQuery;
import platform.merchant.service.application.command.review.FetchMerchantReviewsResult;
import platform.merchant.service.application.service.MerchantReviewService;
import platform.core.common.service.domain.booking.model.Booking;
import platform.core.common.service.domain.booking.port.BookingRepositoryPort;
import platform.core.common.service.application.command.common.PagedResult;
import platform.merchant.service.domain.review.ReviewType;
import platform.merchant.service.domain.review.model.MerchantReview;
import platform.merchant.service.domain.review.port.MerchantReviewRepositoryPort;
import platform.merchant.service.domain.route.port.RouteAggregateRepositoryPort;
import platform.core.common.service.persistence.exception.BusinessException;
import platform.core.common.service.persistence.utils.ApiRequestUtils;
import platform.core.common.service.persistence.utils.ExceptionUtils;

import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.UUID;

import static platform.core.common.service.persistence.constant.ErrorConstant.DUPLICATE_ERROR;
import static platform.core.common.service.persistence.constant.ErrorConstant.INVALID_INPUT_ERROR;
import static platform.core.common.service.persistence.constant.ErrorConstant.INVALID_PAGE_NUMBER;
import static platform.core.common.service.persistence.constant.ErrorConstant.INVALID_PAGE_SIZE;
import static platform.core.common.service.persistence.constant.ErrorConstant.MERCHANT_REVIEW_NOT_FOUND_BY_ID;
import static platform.core.common.service.persistence.constant.ErrorConstant.RECORD_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class MerchantReviewServiceImpl implements MerchantReviewService {

    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final int DEFAULT_PAGE_NUMBER = 1;
    private static final String DUPLICATE_TRIP_REVIEW = "Trip review already exists for this booking and customer";
    private static final String BOOKING_ID_REQUIRED_FOR_TRIP_REVIEW = "bookingId is required for TRIP review";
    private static final String TRIP_REVIEW_REQUIRES_COMPLETED_ROUTE = "TRIP review is allowed only after the trip has been completed";
    private static final String BOOKING_NOT_FOUND = "Booking with Id %s not found";
    private static final String BOOKING_NOT_FOUND_FOR_MERCHANT = "Booking with Id %s not found for merchant";
    private static final String BOOKING_CUSTOMER_MISMATCH = "bookingId does not belong to customerId";
    private static final String TRIP_FIELDS_NOT_ALLOWED_FOR_MERCHANT_REVIEW =
            "driverRating, vehicleRating, punctualityRating, tripExperienceRating and safetyRating are only allowed for TRIP review";
    private static final String MERCHANT_FIELDS_NOT_ALLOWED_FOR_TRIP_REVIEW =
            "merchantServiceRating, staffSupportRating and valueForMoneyRating are only allowed for MERCHANT review";
    private static final String MERCHANT_REVIEW_FIELDS_REQUIRED =
            "merchantServiceRating, staffSupportRating and valueForMoneyRating are required for MERCHANT review";

    private final MerchantReviewRepositoryPort merchantReviewRepositoryPort;
    private final BookingRepositoryPort bookingRepositoryPort;
    private final RouteAggregateRepositoryPort routeAggregateRepositoryPort;

    @Override
    public CreateMerchantReviewResult createReview(CreateMerchantReviewCommand command) {
        validateCreateCommand(command);

        MerchantReview review = MerchantReview.create(
                UUID.randomUUID().toString(),
                command.merchantId().trim(),
                command.reviewType(),
                trimToNull(command.bookingId()),
                trimToNull(command.tripId()),
                trimToNull(command.routeCode()),
                trimToNull(command.driverId()),
                trimToNull(command.vehicleId()),
                trimToNull(command.customerId()),
                trimToNull(command.customerName()),
                command.overallRating(),
                command.driverRating(),
                command.vehicleRating(),
                command.punctualityRating(),
                command.tripExperienceRating(),
                command.safetyRating(),
                command.merchantServiceRating(),
                command.staffSupportRating(),
                command.valueForMoneyRating(),
                trimToNull(command.comment()),
                parseReviewedAt(command),
                trimToNull(command.creator())
        );

        MerchantReview savedReview = merchantReviewRepositoryPort.save(review);
        return CreateMerchantReviewResult.builder()
                .id(savedReview.getId())
                .merchantId(savedReview.getMerchantId())
                .reviewType(savedReview.getReviewType())
                .bookingId(savedReview.getBookingId())
                .tripId(savedReview.getTripId())
                .tripCode(savedReview.getTripCode())
                .driverId(savedReview.getDriverId())
                .vehicleId(savedReview.getVehicleId())
                .customerId(savedReview.getCustomerId())
                .customerName(savedReview.getCustomerName())
                .overallRating(savedReview.getOverallRating())
                .driverRating(savedReview.getDriverRating())
                .vehicleRating(savedReview.getVehicleRating())
                .punctualityRating(savedReview.getPunctualityRating())
                .tripExperienceRating(savedReview.getTripExperienceRating())
                .safetyRating(savedReview.getSafetyRating())
                .merchantServiceRating(savedReview.getMerchantServiceRating())
                .staffSupportRating(savedReview.getStaffSupportRating())
                .valueForMoneyRating(savedReview.getValueForMoneyRating())
                .comment(savedReview.getComment())
                .reviewedAt(savedReview.getReviewedAt().toString())
                .build();
    }

    @Override
    public FetchMerchantReviewsResult fetchReviews(FetchMerchantReviewsQuery query) {
        int pageSize = ApiRequestUtils.parseIntOrDefault(
                query.pageSize(),
                DEFAULT_PAGE_SIZE,
                "pageSize",
                query.context().requestId(),
                query.context().requestDateTime(),
                query.context().channel());
        int pageNumber = ApiRequestUtils.parseIntOrDefault(
                query.pageNumber(),
                DEFAULT_PAGE_NUMBER,
                "pageNumber",
                query.context().requestId(),
                query.context().requestDateTime(),
                query.context().channel());

        if (pageSize < 1 || pageSize > 100) {
            throw new BusinessException(query.context().requestId(), query.context().requestDateTime(), query.context().channel(),
                    ExceptionUtils.buildResultResponse(INVALID_INPUT_ERROR, INVALID_PAGE_SIZE));
        }
        if (pageNumber < 1) {
            throw new BusinessException(query.context().requestId(), query.context().requestDateTime(), query.context().channel(),
                    ExceptionUtils.buildResultResponse(INVALID_INPUT_ERROR, INVALID_PAGE_NUMBER));
        }

        PagedResult<MerchantReview> page = merchantReviewRepositoryPort.fetchByMerchantId(query.merchantId(), pageNumber - 1, pageSize);
        List<FetchMerchantReviewsResult.FetchMerchantReviewItemResult> items = page.getItems().stream()
                .map(review -> FetchMerchantReviewsResult.FetchMerchantReviewItemResult.builder()
                        .id(review.getId())
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
                        .reviewedAt(review.getReviewedAt() == null ? null : review.getReviewedAt().toString())
                        .build())
                .toList();

        return FetchMerchantReviewsResult.builder()
                .items(items)
                .pageNumber(page.getPageNumber() + 1)
                .pageSize(page.getPageSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .totalReviews(merchantReviewRepositoryPort.countByMerchantId(query.merchantId()))
                .averageOverallRating(merchantReviewRepositoryPort.findAverageOverallRatingByMerchantId(query.merchantId()))
                .build();
    }

    @Override
    public FetchMerchantReviewDetailResult fetchReviewDetail(FetchMerchantReviewDetailQuery query) {
        MerchantReview review = merchantReviewRepositoryPort.findById(query.reviewId(), query.merchantId())
                .orElseThrow(() -> new BusinessException(
                        query.context().requestId(),
                        query.context().requestDateTime(),
                        query.context().channel(),
                        ExceptionUtils.buildResultResponse(RECORD_NOT_FOUND, String.format(MERCHANT_REVIEW_NOT_FOUND_BY_ID, query.reviewId()))
                ));

        return FetchMerchantReviewDetailResult.builder()
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
                .reviewedAt(review.getReviewedAt() == null ? null : review.getReviewedAt().toString())
                .build();
    }

    private void validateCreateCommand(CreateMerchantReviewCommand command) {
        if (command.merchantId() == null || command.merchantId().isBlank()) {
            throw invalidInput(command, "merchantId is required");
        }
        if (command.reviewType() == null) {
            throw invalidInput(command, "reviewType is required");
        }
        if (command.customerId() == null || command.customerId().isBlank()) {
            throw invalidInput(command, "customerId is required");
        }

        if (command.reviewType() == ReviewType.TRIP) {
            validateTripReview(command);
        } else {
            validateMerchantReview(command);
        }

        validateRating(command, command.overallRating(), "overallRating", true);
        validateRating(command, command.driverRating(), "driverRating", false);
        validateRating(command, command.vehicleRating(), "vehicleRating", false);
        validateRating(command, command.punctualityRating(), "punctualityRating", false);
        validateRating(command, command.tripExperienceRating(), "tripExperienceRating", false);
        validateRating(command, command.safetyRating(), "safetyRating", false);
        validateRating(command, command.merchantServiceRating(), "merchantServiceRating", false);
        validateRating(command, command.staffSupportRating(), "staffSupportRating", false);
        validateRating(command, command.valueForMoneyRating(), "valueForMoneyRating", false);
    }

    private void validateTripReview(CreateMerchantReviewCommand command) {
        if (command.bookingId() == null || command.bookingId().isBlank()) {
            throw invalidInput(command, BOOKING_ID_REQUIRED_FOR_TRIP_REVIEW);
        }

        if (hasAnyMerchantSpecificField(command)) {
            throw invalidInput(command, MERCHANT_FIELDS_NOT_ALLOWED_FOR_TRIP_REVIEW);
        }

        String bookingId = command.bookingId().trim();
        String merchantId = command.merchantId().trim();
        String customerId = command.customerId().trim();

        Booking booking = bookingRepositoryPort.findById(bookingId, merchantId)
                .or(() -> bookingRepositoryPort.findById(bookingId).map(existingBooking -> {
                    if (existingBooking.getMerchantId() == null || !merchantId.equals(existingBooking.getMerchantId().trim())) {
                        throw new BusinessException(
                                command.context().requestId(),
                                command.context().requestDateTime(),
                                command.context().channel(),
                                ExceptionUtils.buildResultResponse(RECORD_NOT_FOUND, String.format(BOOKING_NOT_FOUND_FOR_MERCHANT, bookingId)));
                    }
                    return existingBooking;
                }))
                .orElseThrow(() -> new BusinessException(
                        command.context().requestId(),
                        command.context().requestDateTime(),
                        command.context().channel(),
                        ExceptionUtils.buildResultResponse(RECORD_NOT_FOUND, String.format(BOOKING_NOT_FOUND, bookingId))));

        if (!customerId.equals(booking.getCustomerId())) {
            throw invalidInput(command, BOOKING_CUSTOMER_MISMATCH);
        }


        // TODO: Refactor to trip entity instead of route
//        boolean completedRoute = routeAggregateRepositoryPort.findById(booking.getRouteId(), merchantId)
//                .map(route -> route.getStatus() == RouteStatus.COMPLETED)
//                .orElse(false);
//        if (!completedRoute) {
//            throw invalidInput(command, TRIP_REVIEW_REQUIRES_COMPLETED_ROUTE);
//        }

        if (merchantReviewRepositoryPort.existsTripReview(
                merchantId,
                bookingId,
                customerId)) {
            throw new BusinessException(
                    command.context().requestId(),
                    command.context().requestDateTime(),
                    command.context().channel(),
                    ExceptionUtils.buildResultResponse(DUPLICATE_ERROR, DUPLICATE_TRIP_REVIEW));
        }
    }

    private void validateMerchantReview(CreateMerchantReviewCommand command) {
        if (hasAnyTripSpecificField(command)) {
            throw invalidInput(command, TRIP_FIELDS_NOT_ALLOWED_FOR_MERCHANT_REVIEW);
        }

        if (command.merchantServiceRating() == null
                || command.staffSupportRating() == null
                || command.valueForMoneyRating() == null) {
            throw invalidInput(command, MERCHANT_REVIEW_FIELDS_REQUIRED);
        }
    }

    private void validateRating(CreateMerchantReviewCommand command, Integer rating, String field, boolean required) {
        if (rating == null) {
            if (required) {
                throw invalidInput(command, field + " is required");
            }
            return;
        }
        if (rating < 1 || rating > 5) {
            throw invalidInput(command, field + " must be in [1..5]");
        }
    }

    private OffsetDateTime parseReviewedAt(CreateMerchantReviewCommand command) {
        if (command.reviewedAt() == null || command.reviewedAt().isBlank()) {
            return null;
        }
        try {
            return OffsetDateTime.parse(command.reviewedAt());
        } catch (DateTimeParseException exception) {
            throw invalidInput(command, "reviewedAt must be a valid ISO-8601 datetime");
        }
    }

    private BusinessException invalidInput(CreateMerchantReviewCommand command, String message) {
        return new BusinessException(
                command.context().requestId(),
                command.context().requestDateTime(),
                command.context().channel(),
                ExceptionUtils.buildResultResponse(INVALID_INPUT_ERROR, message));
    }

    private String trimToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    private boolean hasAnyTripSpecificField(CreateMerchantReviewCommand command) {
        return command.driverRating() != null
                || command.vehicleRating() != null
                || command.punctualityRating() != null
                || command.tripExperienceRating() != null
                || command.safetyRating() != null;
    }

    private boolean hasAnyMerchantSpecificField(CreateMerchantReviewCommand command) {
        return command.merchantServiceRating() != null
                || command.staffSupportRating() != null
                || command.valueForMoneyRating() != null;
    }
}
