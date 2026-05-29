package platform.merchant.service.interfaces.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.com.go.routex.identity.security.log.SystemLog;
import platform.merchant.service.application.command.review.CreateMerchantReviewCommand;
import platform.merchant.service.application.command.review.CreateMerchantReviewResult;
import platform.merchant.service.application.command.review.FetchMerchantReviewDetailQuery;
import platform.merchant.service.application.command.review.FetchMerchantReviewDetailResult;
import platform.merchant.service.application.command.review.FetchMerchantReviewsQuery;
import platform.merchant.service.application.command.review.FetchMerchantReviewsResult;
import platform.merchant.service.application.service.MerchantReviewService;
import platform.core.common.service.persistence.utils.ApiRequestUtils;
import platform.merchant.service.infrastructure.persistence.utils.HttpUtils;
import platform.merchant.service.interfaces.factory.ApiResultFactory;
import platform.core.common.service.api.BaseRequest;
import platform.merchant.service.interfaces.model.review.CreateMerchantReviewRequest;
import platform.merchant.service.interfaces.model.review.CreateMerchantReviewResponse;
import platform.merchant.service.interfaces.model.review.FetchMerchantReviewDetailResponse;
import platform.merchant.service.interfaces.model.review.FetchMerchantReviewsResponse;

import java.util.List;
import java.util.stream.Collectors;

import static platform.core.common.service.persistence.constant.ApiConstant.API_PATH;
import static platform.core.common.service.persistence.constant.ApiConstant.API_VERSION;
import static platform.core.common.service.persistence.constant.ApiConstant.CREATE_PATH;
import static platform.core.common.service.persistence.constant.ApiConstant.DETAIL_PATH;
import static platform.core.common.service.persistence.constant.ApiConstant.FETCH_PATH;
import static platform.core.common.service.persistence.constant.ApiConstant.MERCHANT_SERVICE;
import static platform.core.common.service.persistence.constant.ApiConstant.REVIEWS_PATH;

@RestController
@RequiredArgsConstructor
@RequestMapping(API_PATH + API_VERSION + MERCHANT_SERVICE)
public class MerchantReviewController {

    private final MerchantReviewService merchantReviewService;
    private final ApiResultFactory apiResultFactory;
    private final SystemLog sLog = SystemLog.getLogger(this.getClass());

    @InitBinder
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.setDisallowedFields("requestId", "requestDateTime", "channel", "data");
    }

    @PostMapping(REVIEWS_PATH + CREATE_PATH)
    public ResponseEntity<CreateMerchantReviewResponse> createReview(
            @Valid @RequestBody CreateMerchantReviewRequest request
    ) {
        sLog.info("[MERCHANT-REVIEW] Create Review Request: {}", request);
        CreateMerchantReviewRequest.CreateMerchantReviewRequestData data = request.getData();

        CreateMerchantReviewResult result = merchantReviewService.createReview(CreateMerchantReviewCommand.builder()
                .context(HttpUtils.toContext(request, data.getMerchantId()))
                .merchantId(data.getMerchantId())
                .reviewType(data.getReviewType())
                .bookingId(data.getBookingId())
                .tripId(data.getTripId())
                .routeCode(data.getRouteCode())
                .driverId(data.getDriverId())
                .vehicleId(data.getVehicleId())
                .customerId(data.getCustomerId())
                .customerName(data.getCustomerName())
                .overallRating(data.getOverallRating())
                .driverRating(data.getDriverRating())
                .vehicleRating(data.getVehicleRating())
                .punctualityRating(data.getPunctualityRating())
                .tripExperienceRating(data.getTripExperienceRating())
                .safetyRating(data.getSafetyRating())
                .merchantServiceRating(data.getMerchantServiceRating())
                .staffSupportRating(data.getStaffSupportRating())
                .valueForMoneyRating(data.getValueForMoneyRating())
                .comment(data.getComment())
                .reviewedAt(data.getReviewedAt())
                .creator(data.getCreator())
                .build());

        CreateMerchantReviewResponse response = CreateMerchantReviewResponse.builder()
                .requestId(request.getRequestId())
                .requestDateTime(request.getRequestDateTime())
                .channel(request.getChannel())
                .result(apiResultFactory.buildSuccess())
                .data(CreateMerchantReviewResponse.CreateMerchantReviewResponseData.builder()
                        .id(result.id())
                        .merchantId(result.merchantId())
                        .reviewType(result.reviewType())
                        .bookingId(result.bookingId())
                        .tripId(result.tripId())
                        .tripCode(result.tripCode())
                        .driverId(result.driverId())
                        .vehicleId(result.vehicleId())
                        .customerId(result.customerId())
                        .customerName(result.customerName())
                        .overallRating(result.overallRating())
                        .driverRating(result.driverRating())
                        .vehicleRating(result.vehicleRating())
                        .punctualityRating(result.punctualityRating())
                        .tripExperienceRating(result.tripExperienceRating())
                        .safetyRating(result.safetyRating())
                        .merchantServiceRating(result.merchantServiceRating())
                        .staffSupportRating(result.staffSupportRating())
                        .valueForMoneyRating(result.valueForMoneyRating())
                        .comment(result.comment())
                        .reviewedAt(result.reviewedAt())
                        .build())
                .build();

        return HttpUtils.buildResponse(request, response);
    }

    @GetMapping(REVIEWS_PATH + FETCH_PATH)
    @PreAuthorize("hasAuthority('reviews:management') or hasRole('MERCHANT_OWNER')")
    public ResponseEntity<FetchMerchantReviewsResponse> fetchReviews(
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize,
            HttpServletRequest servletRequest
    ) {
        BaseRequest baseRequest = ApiRequestUtils.getBaseRequestOrDefault(servletRequest);
        String merchantId = ApiRequestUtils.requireMerchantId(servletRequest, baseRequest);

        FetchMerchantReviewsResult result = merchantReviewService.fetchReviews(FetchMerchantReviewsQuery.builder()
                .context(HttpUtils.toContext(baseRequest, merchantId))
                .merchantId(merchantId)
                .pageNumber(String.valueOf(pageNumber))
                .pageSize(String.valueOf(pageSize))
                .build());

        List<FetchMerchantReviewsResponse.FetchMerchantReviewResponseData> items = result.items().stream()
                .map(item -> FetchMerchantReviewsResponse.FetchMerchantReviewResponseData.builder()
                        .id(item.id())
                        .reviewType(item.reviewType())
                        .bookingId(item.bookingId())
                        .tripId(item.tripId())
                        .tripCode(item.tripCode())
                        .driverId(item.driverId())
                        .vehicleId(item.vehicleId())
                        .customerId(item.customerId())
                        .customerName(item.customerName())
                        .overallRating(item.overallRating())
                        .driverRating(item.driverRating())
                        .vehicleRating(item.vehicleRating())
                        .punctualityRating(item.punctualityRating())
                        .tripExperienceRating(item.tripExperienceRating())
                        .safetyRating(item.safetyRating())
                        .merchantServiceRating(item.merchantServiceRating())
                        .staffSupportRating(item.staffSupportRating())
                        .valueForMoneyRating(item.valueForMoneyRating())
                        .comment(item.comment())
                        .reviewedAt(item.reviewedAt())
                        .build())
                .collect(Collectors.toList());

        FetchMerchantReviewsResponse response = FetchMerchantReviewsResponse.builder()
                .requestId(baseRequest.getRequestId())
                .requestDateTime(baseRequest.getRequestDateTime())
                .channel(baseRequest.getChannel())
                .result(apiResultFactory.buildSuccess())
                .data(FetchMerchantReviewsResponse.FetchMerchantReviewsPage.builder()
                        .items(items)
                        .summary(FetchMerchantReviewsResponse.Summary.builder()
                                .totalReviews(result.totalReviews())
                                .averageOverallRating(result.averageOverallRating())
                                .build())
                        .pagination(FetchMerchantReviewsResponse.Pagination.builder()
                                .pageNumber(result.pageNumber())
                                .pageSize(result.pageSize())
                                .totalElements(result.totalElements())
                                .totalPages(result.totalPages())
                                .build())
                        .build())
                .build();

        return HttpUtils.buildResponse(baseRequest, response);
    }

    @GetMapping(REVIEWS_PATH + DETAIL_PATH)
    @PreAuthorize("hasAuthority('reviews:management') or hasRole('MERCHANT_OWNER')")
    public ResponseEntity<FetchMerchantReviewDetailResponse> fetchReviewDetail(
            @RequestParam String reviewId,
            HttpServletRequest servletRequest
    ) {
        BaseRequest baseRequest = ApiRequestUtils.getBaseRequestOrDefault(servletRequest);
        String merchantId = ApiRequestUtils.requireMerchantId(servletRequest, baseRequest);

        FetchMerchantReviewDetailResult result = merchantReviewService.fetchReviewDetail(FetchMerchantReviewDetailQuery.builder()
                .context(HttpUtils.toContext(baseRequest, merchantId))
                .merchantId(merchantId)
                .reviewId(reviewId)
                .build());

        FetchMerchantReviewDetailResponse response = FetchMerchantReviewDetailResponse.builder()
                .requestId(baseRequest.getRequestId())
                .requestDateTime(baseRequest.getRequestDateTime())
                .channel(baseRequest.getChannel())
                .result(apiResultFactory.buildSuccess())
                .data(FetchMerchantReviewDetailResponse.FetchMerchantReviewDetailResponseData.builder()
                        .id(result.id())
                        .merchantId(result.merchantId())
                        .reviewType(result.reviewType())
                        .bookingId(result.bookingId())
                        .tripId(result.tripId())
                        .tripCode(result.tripCode())
                        .driverId(result.driverId())
                        .vehicleId(result.vehicleId())
                        .customerId(result.customerId())
                        .customerName(result.customerName())
                        .overallRating(result.overallRating())
                        .driverRating(result.driverRating())
                        .vehicleRating(result.vehicleRating())
                        .punctualityRating(result.punctualityRating())
                        .tripExperienceRating(result.tripExperienceRating())
                        .safetyRating(result.safetyRating())
                        .merchantServiceRating(result.merchantServiceRating())
                        .staffSupportRating(result.staffSupportRating())
                        .valueForMoneyRating(result.valueForMoneyRating())
                        .comment(result.comment())
                        .reviewedAt(result.reviewedAt())
                        .build())
                .build();

        return HttpUtils.buildResponse(baseRequest, response);
    }
}
