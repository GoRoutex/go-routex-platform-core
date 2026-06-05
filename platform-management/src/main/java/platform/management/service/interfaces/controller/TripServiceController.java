package platform.management.service.interfaces.controller;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import platform.core.common.service.api.BaseRequest;
import platform.core.common.service.application.command.common.PageContext;
import platform.management.service.application.command.route.FetchTripQuery;
import platform.management.service.application.command.route.FetchTripResult;
import platform.management.service.application.command.route.FetchTripsQuery;
import platform.management.service.application.command.route.FetchTripsResult;
import platform.management.service.application.command.route.SearchTripQuery;
import platform.management.service.application.command.route.SearchTripResult;
import platform.management.service.application.services.TripManagementService;
import platform.management.service.infrastructure.persistence.utils.ApiRequestUtils;
import platform.management.service.infrastructure.persistence.utils.HttpUtils;
import platform.management.service.interfaces.factory.ApiResultFactory;
import platform.management.service.interfaces.mapper.TripResponseMapper;
import platform.management.service.interfaces.models.route.SearchTripRequest;
import platform.management.service.interfaces.models.route.SearchTripResponse;
import platform.management.service.interfaces.models.trip.FetchTripDetailResponse;
import platform.management.service.interfaces.models.trip.FetchTripResponse;
import vn.com.go.routex.identity.security.log.SystemLog;

import static platform.core.common.service.persistence.constant.ApiConstant.API_PATH;
import static platform.core.common.service.persistence.constant.ApiConstant.API_VERSION;
import static platform.core.common.service.persistence.constant.ApiConstant.DETAIL_PATH;
import static platform.core.common.service.persistence.constant.ApiConstant.FETCH_PATH;
import static platform.core.common.service.persistence.constant.ApiConstant.MANAGEMENT_PATH;
import static platform.core.common.service.persistence.constant.ApiConstant.SEARCH_PATH;
import static platform.core.common.service.persistence.constant.ApiConstant.TRIP_SERVICE;


@RestController
@RequestMapping(API_PATH + API_VERSION + MANAGEMENT_PATH + TRIP_SERVICE)
@RequiredArgsConstructor
public class TripServiceController {

    private final TripManagementService tripManagementService;
    private final ApiResultFactory apiResultFactory;
    private final TripResponseMapper tripResponseMapper;
    private final SystemLog sLog = SystemLog.getLogger(this.getClass());

    @PostMapping(SEARCH_PATH)
    public ResponseEntity<SearchTripResponse> searchRoute(@Valid @RequestBody SearchTripRequest request) {
        sLog.info("[TRIP-SERVICE] Search Trip Request: {}", request);
        SearchTripResult result = tripManagementService.searchTrip(SearchTripQuery.builder()
                .originName(request.getData().getOrigin())
                .destinationName(request.getData().getDestination())
                .departureDate(request.getData().getDepartureDate())
                .seat(request.getData().getSeat())
                .pageContext(PageContext.builder()
                        .pageSize(request.getData().getPageSize())
                        .pageNumber(request.getData().getPageNumber())
                        .build())
                .context(HttpUtils.toContext(request))
                .build());

        SearchTripResponse response = SearchTripResponse.builder()
                .result(apiResultFactory.buildSuccess())
                .data(result.data().stream()
                        .map(tripResponseMapper::toSearchTripResponseData)
                        .toList())
                .build();

        return HttpUtils.buildResponse(request, response);
    }

    @GetMapping(DETAIL_PATH)
    public ResponseEntity<FetchTripDetailResponse> fetchRouteDetail(
            HttpServletRequest servletRequest,
            @RequestParam String tripId
    ) {
        BaseRequest baseRequest = ApiRequestUtils.getBaseRequestOrDefault(servletRequest);
        sLog.info("[TRIP-SERVICE] Fetch Trip Detail Request tripId: {}", tripId);

        FetchTripResult result = tripManagementService.fetchTripDetail(FetchTripQuery.builder()
                .tripId(tripId)
                .requestId(baseRequest.getRequestId())
                .requestDateTime(baseRequest.getRequestDateTime())
                .channel(baseRequest.getChannel())
                .build());

        FetchTripDetailResponse response = FetchTripDetailResponse.builder()
                .result(apiResultFactory.buildSuccess())
                .data(tripResponseMapper.toFetchTripDetailResponseData(result))
                .build();

        return HttpUtils.buildResponse(baseRequest, response);
    }

    @GetMapping(FETCH_PATH)
    public ResponseEntity<FetchTripResponse> fetchTrips(
            HttpServletRequest servletRequest,
            @RequestParam(required = false) String merchantName,
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        BaseRequest baseRequest = ApiRequestUtils.getBaseRequestOrDefault(servletRequest);
        String merchantId = ApiRequestUtils.getMerchantId(servletRequest);
        FetchTripsResult result = tripManagementService.fetchTrips(FetchTripsQuery.builder()
                .pageContext(PageContext.builder()
                        .pageNumber(String.valueOf(pageNumber))
                        .pageSize(String.valueOf(pageSize))
                        .build())
                .merchantId(merchantId)
                .merchantName(merchantName)
                .context(HttpUtils.toContext(baseRequest))
                .build());


        FetchTripResponse response = FetchTripResponse.builder()
                .result(apiResultFactory.buildSuccess())
                .data(FetchTripResponse.FetchTripResponsePage.builder()
                        .items(result.items().stream()
                                .map(tripResponseMapper::toPublicFetchTripResponseData)
                                .toList())

                        .pagination(FetchTripResponse.Pagination.builder()
                                .pageNumber(result.pageNumber())
                                .pageSize(result.pageSize())
                                .totalElements(result.totalElements())
                                .totalPages(result.totalPages())
                                .build())
                        .build())
                .build();

        return HttpUtils.buildResponse(baseRequest, response);
    }
}
