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
import platform.core.common.service.api.BaseRequest;
import platform.core.common.service.domain.trip.TripStatus;
import platform.core.common.service.persistence.utils.ApiRequestUtils;
import platform.merchant.service.application.command.route.AssignRouteBatchCommand;
import platform.merchant.service.application.command.route.AssignRouteBatchResult;
import platform.merchant.service.application.command.route.AssignRouteCommand;
import platform.merchant.service.application.command.route.AssignRouteResult;
import platform.merchant.service.application.command.trip.CreateTripBatchCommand;
import platform.merchant.service.application.command.trip.CreateTripBatchResult;
import platform.merchant.service.application.command.trip.CreateTripCommand;
import platform.merchant.service.application.command.trip.CreateTripResult;
import platform.merchant.service.application.command.trip.DeleteTripCommand;
import platform.merchant.service.application.command.trip.DeleteTripResult;
import platform.merchant.service.application.command.trip.FetchTripDetailQuery;
import platform.merchant.service.application.command.trip.FetchTripDetailResult;
import platform.merchant.service.application.command.trip.FetchTripListQuery;
import platform.merchant.service.application.command.trip.FetchTripListResult;
import platform.merchant.service.application.command.trip.ScheduleAsyncCommand;
import platform.merchant.service.application.command.trip.ScheduleAsyncResult;
import platform.merchant.service.application.command.trip.UpdateTripCommand;
import platform.merchant.service.application.command.trip.UpdateTripResult;
import platform.merchant.service.application.service.MerchantTripService;
import platform.merchant.service.infrastructure.persistence.utils.HttpUtils;
import platform.merchant.service.interfaces.factory.ApiResultFactory;
import platform.merchant.service.interfaces.model.assignment.AssignRouteBatchRequest;
import platform.merchant.service.interfaces.model.assignment.AssignRouteBatchResponse;
import platform.merchant.service.interfaces.model.assignment.AssignRouteResponse;
import platform.merchant.service.interfaces.model.trip.CreateTripBatchRequest;
import platform.merchant.service.interfaces.model.trip.CreateTripBatchResponse;
import platform.merchant.service.interfaces.model.trip.CreateTripRequest;
import platform.merchant.service.interfaces.model.trip.CreateTripResponse;
import platform.merchant.service.interfaces.model.trip.DeleteTripRequest;
import platform.merchant.service.interfaces.model.trip.DeleteTripResponse;
import platform.merchant.service.interfaces.model.trip.FetchTripDetailResponse;
import platform.merchant.service.interfaces.model.trip.FetchTripListResponse;
import platform.merchant.service.interfaces.model.trip.ScheduleAsyncRequest;
import platform.merchant.service.interfaces.model.trip.ScheduleAsyncResponse;
import platform.merchant.service.interfaces.model.trip.UpdateTripRequest;
import platform.merchant.service.interfaces.model.trip.UpdateTripResponse;
import vn.com.go.routex.identity.security.log.SystemLog;

import java.util.List;
import java.util.stream.Collectors;

import static platform.core.common.service.persistence.constant.ApiConstant.API_PATH;
import static platform.core.common.service.persistence.constant.ApiConstant.API_VERSION;
import static platform.core.common.service.persistence.constant.ApiConstant.ASSIGNMENT_PATH;
import static platform.core.common.service.persistence.constant.ApiConstant.BATCH_ASSIGNMENT_PATH;
import static platform.core.common.service.persistence.constant.ApiConstant.BATCH_CREATE_PATH;
import static platform.core.common.service.persistence.constant.ApiConstant.CREATE_PATH;
import static platform.core.common.service.persistence.constant.ApiConstant.DELETE_PATH;
import static platform.core.common.service.persistence.constant.ApiConstant.DETAIL_PATH;
import static platform.core.common.service.persistence.constant.ApiConstant.FETCH_PATH;
import static platform.core.common.service.persistence.constant.ApiConstant.MERCHANT_SERVICE;
import static platform.core.common.service.persistence.constant.ApiConstant.SCHEDULE_ASYNC_PATH;
import static platform.core.common.service.persistence.constant.ApiConstant.TRIPS_PATH;
import static platform.core.common.service.persistence.constant.ApiConstant.UPDATE_PATH;


@RestController
@RequestMapping(API_PATH + API_VERSION + MERCHANT_SERVICE)
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('trip:management') or hasRole('MERCHANT_OWNER')")
public class MerchantTripController {

    private final MerchantTripService merchantTripService;
    private final ApiResultFactory apiResultFactory;
    private final SystemLog sLog = SystemLog.getLogger(this.getClass());

    @InitBinder
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.setDisallowedFields("requestId", "requestDateTime", "channel", "data");
    }

    @PostMapping(TRIPS_PATH + ASSIGNMENT_PATH)
    public ResponseEntity<AssignRouteResponse> assignRoute(@Valid @RequestBody platform.merchant.service.interfaces.model.assignment.AssignRouteRequest request,
                                                           HttpServletRequest servletRequest) {
        sLog.info("[ASSIGN-ROUTE] Assign Route Request: {}", request);
        String merchantId = ApiRequestUtils.requireMerchantId(servletRequest, request);
        AssignRouteResult result = merchantTripService.assignRoute(AssignRouteCommand.builder()
                .merchantId(merchantId)
                .creator(request.getData().getCreator())
                .tripId(request.getData().getTripId())
                .vehicleId(request.getData().getVehicleId())
                .driverId(request.getData().getDriverId())
                .context(HttpUtils.toContext(request, merchantId))
                .build());

        AssignRouteResponse response = AssignRouteResponse.builder()
                .requestId(request.getRequestId())
                .requestDateTime(request.getRequestDateTime())
                .channel(request.getChannel())
                .result(apiResultFactory.buildSuccess())
                .data(AssignRouteResponse.AssignRouteResponseData.builder()
                        .creator(result.creator())
                        .tripId(result.tripId())
                        .vehicleId(result.vehicleId())
                        .assignedAt(result.assignedAt())
                        .status(result.status())
                        .build())
                .build();

        sLog.info("[ASSIGN-ROUTE] Assign Route Response: {}", response);
        return HttpUtils.buildResponse(request, response);
    }

    @PostMapping(TRIPS_PATH + BATCH_ASSIGNMENT_PATH)
    public ResponseEntity<AssignRouteBatchResponse> assignRouteBatch(@Valid @RequestBody AssignRouteBatchRequest request,
                                                                     HttpServletRequest servletRequest) {
        sLog.info("[ASSIGN-BATCH] Batch Assign Request: {} items", request.getData().getAssignments().size());
        String merchantId = ApiRequestUtils.requireMerchantId(servletRequest, request);

        AssignRouteBatchResult result = merchantTripService.assignRouteBatch(AssignRouteBatchCommand.builder()
                .merchantId(merchantId)
                .creator(request.getData().getCreator())
                .context(HttpUtils.toContext(request, merchantId))
                .assignments(request.getData().getAssignments().stream()
                        .map(item -> AssignRouteBatchCommand.AssignRouteBatchItem.builder()
                                .tripId(item.getTripId())
                                .vehicleId(item.getVehicleId())
                                .driverId(item.getDriverId())
                                .build())
                        .collect(Collectors.toList()))
                .build());

        AssignRouteBatchResponse response = AssignRouteBatchResponse.builder()
                .requestId(request.getRequestId())
                .requestDateTime(request.getRequestDateTime())
                .channel(request.getChannel())
                .result(apiResultFactory.buildSuccess())
                .data(AssignRouteBatchResponse.AssignRouteBatchResponseData.builder()
                        .successCount(result.successCount())
                        .failedCount(result.failedCount())
                        .successItems(result.successItems().stream()
                                .map(s -> AssignRouteBatchResponse.SuccessItem.builder()
                                        .tripId(s.tripId())
                                        .vehicleId(s.vehicleId())
                                        .driverId(s.driverId())
                                        .assignedAt(s.assignedAt())
                                        .status(s.status())
                                        .build())
                                .collect(Collectors.toList()))
                        .failedItems(result.failedItems().stream()
                                .map(f -> AssignRouteBatchResponse.FailedItem.builder()
                                        .tripId(f.tripId())
                                        .driverId(f.driverId())
                                        .vehicleId(f.vehicleId())
                                        .errorCode(f.errorCode())
                                        .errorMessage(f.errorMessage())
                                        .build())
                                .collect(Collectors.toList()))
                        .build())
                .build();

        sLog.info("[ASSIGN-BATCH] Batch Assign Response: Success={}, Failed={}", result.successCount(), result.failedCount());
        return HttpUtils.buildResponse(request, response);
    }
    @PostMapping(TRIPS_PATH + SCHEDULE_ASYNC_PATH)
    public ResponseEntity<ScheduleAsyncResponse> scheduleAsync(@Valid @RequestBody ScheduleAsyncRequest request,
                                                               HttpServletRequest servletRequest) {
        sLog.info("[SCHEDULE-ASYNC] Schedule Async Request: Route={}", request.getData().getRouteId());
        String merchantId = ApiRequestUtils.requireMerchantId(servletRequest, request);

        ScheduleAsyncResult result = merchantTripService.scheduleAsync(ScheduleAsyncCommand.builder()
                .merchantId(merchantId)
                .routeId(request.getData().getRouteId())
                .context(HttpUtils.toContext(request, merchantId, request.getUserEmail()))
                .demands(request.getData().getDemands().stream()
                        .map(d -> ScheduleAsyncCommand.DemandEntry.builder()
                                .date(d.getDate())
                                .demand(d.getDemand())
                                .build())
                        .collect(Collectors.toList()))
                .operatingHours(request.getData().getOperatingHours())
                .operatingCostPerTrip(request.getData().getOperatingCostPerTrip())
                .maxTripsAllowed(request.getData().getMaxTripsAllowed())
                .minLoadFactor(request.getData().getMinLoadFactor())
                .build());

        ScheduleAsyncResponse response = ScheduleAsyncResponse.builder()
                .requestId(request.getRequestId())
                .requestDateTime(request.getRequestDateTime())
                .channel(request.getChannel())
                .result(apiResultFactory.buildSuccess())
                .data(ScheduleAsyncResponse.ScheduleAsyncResponseData.builder()
                        .jobId(result.jobId())
                        .status(result.status())
                        .build())
                .build();

        sLog.info("[SCHEDULE-ASYNC] Schedule Async Response: JobId={}, Status={}", result.jobId(), result.status());
        return HttpUtils.buildResponse(request, response);
    }



    @PostMapping(TRIPS_PATH + CREATE_PATH)
    public ResponseEntity<CreateTripResponse> createTrip(@Valid @RequestBody CreateTripRequest request,
                                                         HttpServletRequest servletRequest) {
        sLog.info("[TRIP-MANAGEMENT] Create Trip Request: {}", request);
        String merchantId = ApiRequestUtils.requireMerchantId(servletRequest, request);

        CreateTripResult result = merchantTripService.createTrip(CreateTripCommand.builder()
                        .context(HttpUtils.toContext(request, merchantId))
                        .routeId(request.getData().getRouteId())
                        .merchantId(merchantId)
                        .departureTime(request.getData().getDepartureTime())
                        .rawDepartureDate(request.getData().getRawDepartureDate())
                        .rawDepartureTime(request.getData().getRawDepartureTime())
                        .build());

        CreateTripResponse response = CreateTripResponse.builder()
                .result(apiResultFactory.buildSuccess())
                .data(CreateTripResponse.CreateTripResponseData.builder()
                        .tripId(result.tripId())
                        .routeId(result.routeId())
                        .merchantId(result.merchantId())
                        .departureTime(result.departureTime())
                        .rawDepartureTime(result.rawDepartureTime())
                        .rawDepartureDate(result.rawDepartureDate())
                        .status(result.status())
                        .build())
                .build();
        return HttpUtils.buildResponse(request, response);
    }

    @PostMapping(TRIPS_PATH + BATCH_CREATE_PATH)
    public ResponseEntity<CreateTripBatchResponse> createTripBatch(@Valid @RequestBody CreateTripBatchRequest request,
                                                                   HttpServletRequest servletRequest) {
        sLog.info("[TRIP-MANAGEMENT] Create Trip Batch Request: routeId={}, count={}", 
            request.getData().getRouteId(), request.getData().getTrips().size());
        String merchantId = ApiRequestUtils.requireMerchantId(servletRequest, request);

        CreateTripBatchResult result = merchantTripService.createTripBatch(CreateTripBatchCommand.builder()
                .context(HttpUtils.toContext(request, merchantId))
                .routeId(request.getData().getRouteId())
                .merchantId(merchantId)
                .trips(request.getData().getTrips().stream()
                        .map(t -> CreateTripBatchCommand.TripBatchCommandData.builder()
                                .departureTime(t.getDepartureTime())
                                .rawDepartureDate(t.getRawDepartureDate())
                                .rawDepartureTime(t.getRawDepartureTime())
                                .build())
                        .collect(java.util.stream.Collectors.toList()))
                .build());

        CreateTripBatchResponse response = CreateTripBatchResponse.builder()
                .result(apiResultFactory.buildSuccess())
                .data(CreateTripBatchResponse.CreateTripBatchResponseData.builder()
                        .routeId(result.routeId())
                        .tripIds(result.tripIds())
                        .build())
                .build();
        return HttpUtils.buildResponse(request, response);
    }

    @PostMapping(TRIPS_PATH + UPDATE_PATH)
    public ResponseEntity<UpdateTripResponse> updateTrip(@Valid @RequestBody UpdateTripRequest request,
                                                         HttpServletRequest servletRequest) {
        sLog.info("[TRIP-MANAGEMENT] Update Trip Request: {}", request);
        String merchantId = ApiRequestUtils.requireMerchantId(servletRequest, request);

        UpdateTripResult result = merchantTripService.updateTrip(UpdateTripCommand.builder()
                .context(HttpUtils.toContext(request, merchantId))
                .tripId(request.getData().getTripId())
                .routeId(request.getData().getRouteId())
                .merchantId(merchantId)
                .pickupBranch(request.getData().getPickupBranch())
                .departureTime(request.getData().getDepartureTime())
                .rawDepartureTime(request.getData().getRawDepartureTime())
                .rawDepartureDate(request.getData().getRawDepartureDate())
                .build());

        UpdateTripResponse response = UpdateTripResponse.builder()
                .result(apiResultFactory.buildSuccess())
                .data(UpdateTripResponse.UpdateTripResponseData.builder()
                        .tripId(result.tripId())
                        .routeId(result.routeId())
                        .merchantId(result.merchantId())
                        .departureTime(result.departureTime())
                        .rawDepartureTime(result.rawDepartureTime())
                        .rawDepartureDate(result.rawDepartureDate())
                        .status(result.status())
                        .build())
                .build();
        return HttpUtils.buildResponse(request, response);
    }

    @PostMapping(TRIPS_PATH + DELETE_PATH)
    public ResponseEntity<DeleteTripResponse> deleteTrip(@Valid @RequestBody DeleteTripRequest request,
                                                         HttpServletRequest servletRequest) {
        sLog.info("[TRIP-MANAGEMENT] Delete Trip Request: {}", request);
        String merchantId = ApiRequestUtils.requireMerchantId(servletRequest, request);

        DeleteTripResult result = merchantTripService.deleteTrip(DeleteTripCommand.builder()
                .context(HttpUtils.toContext(request, merchantId))
                .tripId(request.getData().getTripId())
                .merchantId(merchantId)
                .build());

        DeleteTripResponse response = DeleteTripResponse.builder()
                .result(apiResultFactory.buildSuccess())
                .data(DeleteTripResponse.DeleteTripResponseData.builder()
                        .tripId(result.tripId())
                        .status(result.status())
                        .build())
                .build();
        return HttpUtils.buildResponse(request, response);
    }

    @GetMapping(TRIPS_PATH + DETAIL_PATH)
    public ResponseEntity<FetchTripDetailResponse> fetchDetail(
            @RequestParam String tripId,
            @RequestParam(required = false) TripStatus status,
            HttpServletRequest servletRequest
    ) {
        BaseRequest baseRequest = ApiRequestUtils.getBaseRequestOrDefault(servletRequest);
        String merchantId = ApiRequestUtils.requireMerchantId(servletRequest, baseRequest);

        FetchTripDetailResult result = merchantTripService.fetchDetail(FetchTripDetailQuery.builder()
                .context(HttpUtils.toContext(baseRequest, merchantId))
                .tripId(tripId)
                .merchantId(merchantId)
                .status(status)
                .build());

        FetchTripDetailResponse response = FetchTripDetailResponse.builder()
                .requestId(baseRequest.getRequestId())
                .requestDateTime(baseRequest.getRequestDateTime())
                .channel(baseRequest.getChannel())
                .result(apiResultFactory.buildSuccess())
                .data(FetchTripDetailResponse.FetchTripDetailResponseData.builder()
                        .tripId(result.tripId())
                        .tripCode(result.tripCode())
                        .creator(result.creator())
                        .departureTime(result.departureTime())
                        .rawDepartureTime(result.rawDepartureTime())
                        .rawDepartureDate(result.rawDepartureDate())
                        .rawArrivalTime(result.rawArrivalTime())
                        .status(result.status())
                        .route(FetchTripDetailResponse.FetchTripRouteData.builder()
                                .routeId(result.route().routeId())
                                .originName(result.route().originName())
                                .originCode(result.route().originCode())
                                .destinationCode(result.route().destinationCode())
                                .destinationName(result.route().destinationName())
                                .originDepartmentId(result.route().originDepartmentId())
                                .destinationDepartmentId(result.route().destinationDepartmentId())
                                .duration(result.route().duration())
                                .build())
                        .build())
                .build();

        return HttpUtils.buildResponse(baseRequest, response);
    }

    @GetMapping(TRIPS_PATH + FETCH_PATH)
    public ResponseEntity<FetchTripListResponse> fetchList(
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) TripStatus status,
            @RequestParam(required = false) String rawDepartureDate,
            HttpServletRequest servletRequest
    ) {
        BaseRequest baseRequest = ApiRequestUtils.getBaseRequestOrDefault(servletRequest);
        String merchantId = ApiRequestUtils.requireMerchantId(servletRequest, baseRequest);

        FetchTripListResult result = merchantTripService.fetchTripList(FetchTripListQuery.builder()
                .context(HttpUtils.toContext(baseRequest, merchantId))
                .status(status)
                .rawDepartureDate(rawDepartureDate)
                .pageNumber(String.valueOf(pageNumber))
                .pageSize(String.valueOf(pageSize))
                .build());

        List<FetchTripListResponse.FetchTripListResponseData> items = result.items().stream()
                .map(item -> FetchTripListResponse.FetchTripListResponseData.builder()
                        .tripId(item.tripId())
                        .tripCode(item.tripCode())
                        .creator(item.creator())
                        .departureTime(item.departureTime())
                        .rawDepartureDate(item.rawDepartureDate())
                        .rawDepartureTime(item.rawDepartureTime())
                        .rawArrivalTime(item.rawArrivalTime())
                        .status(item.status())
                        .route(FetchTripListResponse.FetchTripListRouteData.builder()
                                .routeId(item.route().routeId())
                                .originName(item.route().originName())
                                .originCode(item.route().originCode())
                                .originDepartmentId(item.route().originDepartmentId())
                                .destinationName(item.route().destinationName())
                                .destinationCode(item.route().destinationCode())
                                .destinationDepartmentId(item.route().destinationDepartmentId())
                                .duration(item.route().duration())
                                .build())
                        .build())
                .collect(Collectors.toList());

        FetchTripListResponse response = FetchTripListResponse.builder()
                .requestId(baseRequest.getRequestId())
                .requestDateTime(baseRequest.getRequestDateTime())
                .channel(baseRequest.getChannel())
                .result(apiResultFactory.buildSuccess())
                .data(FetchTripListResponse.FetchTripListResponsePage.builder()
                        .items(items)
                        .pagination(FetchTripListResponse.Pagination.builder()
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
