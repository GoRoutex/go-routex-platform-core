package platform.management.service.interfaces.controller;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import platform.core.common.service.api.BaseRequest;
import platform.core.common.service.application.command.common.PageContext;
import platform.core.common.service.common.RequestContext;
import platform.management.service.application.command.trip.FetchRoundTripDetailQuery;
import platform.management.service.application.command.trip.FetchRoundTripDetailResult;
import platform.management.service.application.command.trip.FetchTripQuery;
import platform.management.service.application.command.trip.FetchTripResult;
import platform.management.service.application.command.trip.FetchTripsQuery;
import platform.management.service.application.command.trip.FetchTripsResult;
import platform.management.service.application.command.trip.SearchRoundTripDetailQuery;
import platform.management.service.application.command.trip.SearchRoundTripQuery;
import platform.management.service.application.command.trip.SearchRoundTripResult;
import platform.management.service.application.command.trip.SearchTripQuery;
import platform.management.service.application.command.trip.SearchTripResult;
import platform.management.service.application.services.TripManagementService;
import platform.management.service.infrastructure.persistence.utils.ApiRequestUtils;
import platform.management.service.infrastructure.persistence.utils.HttpUtils;
import platform.management.service.interfaces.factory.ApiResultFactory;
import platform.management.service.interfaces.mapper.TripResponseMapper;
import platform.management.service.interfaces.models.trip.FetchRoundTripDetailResponse;
import platform.management.service.interfaces.models.trip.FetchTripDetailResponse;
import platform.management.service.interfaces.models.trip.FetchTripResponse;
import platform.management.service.interfaces.models.trip.SearchRoundTripRequest;
import platform.management.service.interfaces.models.trip.SearchRoundTripResponse;
import platform.management.service.interfaces.models.trip.SearchTripRequest;
import platform.management.service.interfaces.models.trip.SearchTripResponse;
import vn.com.go.routex.identity.security.log.SystemLog;

import static platform.core.common.service.persistence.constant.ApiConstant.API_PATH;
import static platform.core.common.service.persistence.constant.ApiConstant.API_VERSION;
import static platform.core.common.service.persistence.constant.ApiConstant.DETAIL_PATH;
import static platform.core.common.service.persistence.constant.ApiConstant.FETCH_PATH;
import static platform.core.common.service.persistence.constant.ApiConstant.MANAGEMENT_PATH;
import static platform.core.common.service.persistence.constant.ApiConstant.ROUND_TRIP_PATH;
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


    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.setDisallowedFields("requestId", "requestDateTime", "channel", "data");
    }

    @PostMapping(SEARCH_PATH + ROUND_TRIP_PATH)
    public ResponseEntity<SearchRoundTripResponse> searchRoundTrip(@Valid @RequestBody SearchRoundTripRequest request) {

        SearchRoundTripRequest.SearchRoundTripDetailData outBoundRequest = request.getData().getOutboundData();
        SearchRoundTripRequest.SearchRoundTripDetailData returnRequest = request.getData().getReturnData();

        sLog.info("[TRIP-SERVICE] Search Round Trip Request: {}", request);
        SearchRoundTripResult result = tripManagementService.searchRoundTrip(SearchRoundTripQuery.builder()
                .outBoundTrip(SearchRoundTripDetailQuery.builder()
                        .originName(outBoundRequest.getOrigin())
                        .destinationName(outBoundRequest.getDestination())
                        .departureDate(outBoundRequest.getDepartureDate())
                        .build())
                .returnTrip(SearchRoundTripDetailQuery.builder()
                        .originName(returnRequest.getOrigin())
                        .destinationName(returnRequest.getDestination())
                        .departureDate(returnRequest.getDepartureDate())
                        .build())
                .seat(request.getData().getSeat())
                .pageContext(PageContext.builder()
                        .pageSize(request.getData().getPageSize())
                        .pageNumber(request.getData().getPageNumber())
                        .build())
                .context(HttpUtils.toContext(request))
                .build());

        SearchRoundTripResponse response = SearchRoundTripResponse.builder()
                .requestId(request.getRequestId())
                .requestDateTime(request.getRequestDateTime())
                .channel(request.getChannel())
                .result(apiResultFactory.buildSuccess())
                .data(tripResponseMapper.toSearchRoundTripResponseData(result))
                .build();


        return HttpUtils.buildResponse(request, response);
    }
    @PostMapping(SEARCH_PATH)
    public ResponseEntity<SearchTripResponse> searchTrip(@Valid @RequestBody SearchTripRequest request) {
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

        RequestContext context = RequestContext.builder()
                .requestId(baseRequest.getRequestId())
                .requestDateTime(baseRequest.getRequestDateTime())
                .channel(baseRequest.getChannel())
                .build();

        FetchTripResult result = tripManagementService.fetchTripDetail(FetchTripQuery.builder()
                .tripId(tripId)
                .context(context)
                .build());

        FetchTripDetailResponse response = FetchTripDetailResponse.builder()
                .requestId(baseRequest.getRequestId())
                .requestDateTime(baseRequest.getRequestDateTime())
                .channel(baseRequest.getChannel())
                .result(apiResultFactory.buildSuccess())
                .data(tripResponseMapper.toFetchTripDetailResponseData(result))
                .build();

        return HttpUtils.buildResponse(baseRequest, response);
    }

    @GetMapping(DETAIL_PATH + ROUND_TRIP_PATH)
    public ResponseEntity<FetchRoundTripDetailResponse> fetchRoundTripDetail(
            HttpServletRequest servletRequest,
            @RequestParam String outboundTripId,
            @RequestParam String returnTripId
    ) {
        BaseRequest baseRequest = ApiRequestUtils.getBaseRequestOrDefault(servletRequest);
        sLog.info("[TRIP-SERVICE] Fetch Round Trip Detail Request outboundTripId={} returnTripId={}", outboundTripId, returnTripId);

        RequestContext context = RequestContext.builder()
                .requestId(baseRequest.getRequestId())
                .requestDateTime(baseRequest.getRequestDateTime())
                .channel(baseRequest.getChannel())
                .build();
        FetchRoundTripDetailResult result = tripManagementService.fetchRoundTripDetail(FetchRoundTripDetailQuery.builder()
                .outboundTripId(outboundTripId)
                .returnTripId(returnTripId)
                        .context(context)
                .build());

        FetchRoundTripDetailResponse response = FetchRoundTripDetailResponse.builder()
                .requestId(baseRequest.getRequestId())
                .requestDateTime(baseRequest.getRequestDateTime())
                .channel(baseRequest.getChannel())
                .result(apiResultFactory.buildSuccess())
                .data(tripResponseMapper.toFetchRoundTripDetailResponseData(result))
                .build();

        return HttpUtils.buildResponse(baseRequest, response);
    }

    @GetMapping(FETCH_PATH)
    public ResponseEntity<FetchTripResponse> fetchTrips(
            HttpServletRequest servletRequest,
            @RequestParam(required = false) String dateFilter,
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        BaseRequest baseRequest = ApiRequestUtils.getBaseRequestOrDefault(servletRequest);
        FetchTripsResult result = tripManagementService.fetchTrips(FetchTripsQuery.builder()
                .pageContext(PageContext.builder()
                        .pageNumber(String.valueOf(pageNumber))
                        .pageSize(String.valueOf(pageSize))
                        .build())
                .dateFilter(dateFilter)
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
