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
import platform.merchant.service.application.command.route.CreateRouteCommand;
import platform.merchant.service.application.command.route.CreateRouteResult;
import platform.merchant.service.application.command.route.DeleteRouteCommand;
import platform.merchant.service.application.command.route.DeleteRouteResult;
import platform.merchant.service.application.command.route.FetchDetailRouteQuery;
import platform.merchant.service.application.command.route.FetchDetailRouteResult;
import platform.merchant.service.application.command.route.FetchRoutesQuery;
import platform.merchant.service.application.command.route.FetchRoutesResult;
import platform.merchant.service.application.command.route.RoutePointCommand;
import platform.merchant.service.application.command.route.UpdateRouteCommand;
import platform.merchant.service.application.command.route.UpdateRouteResult;
import platform.merchant.service.application.service.RouteManagementService;
import platform.merchant.service.domain.route.RouteStatus;
import platform.core.common.service.persistence.utils.ApiRequestUtils;
import platform.merchant.service.infrastructure.persistence.utils.HttpUtils;
import platform.merchant.service.interfaces.factory.ApiResultFactory;
import platform.merchant.service.interfaces.mapper.RouteResponseMapper;
import platform.core.common.service.api.BaseRequest;
import platform.merchant.service.interfaces.model.route.CreateRouteRequest;
import platform.merchant.service.interfaces.model.route.CreateRouteResponse;
import platform.merchant.service.interfaces.model.route.DeleteRouteRequest;
import platform.merchant.service.interfaces.model.route.DeleteRouteResponse;
import platform.merchant.service.interfaces.model.route.FetchDetailRouteResponse;
import platform.merchant.service.interfaces.model.route.FetchRouteResponse;
import platform.merchant.service.interfaces.model.route.SearchRouteResponse;
import platform.merchant.service.interfaces.model.route.UpdateRouteRequest;
import platform.merchant.service.interfaces.model.route.UpdateRouteResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static platform.core.common.service.persistence.constant.ApiConstant.API_PATH;
import static platform.core.common.service.persistence.constant.ApiConstant.API_VERSION;
import static platform.core.common.service.persistence.constant.ApiConstant.CREATE_PATH;
import static platform.core.common.service.persistence.constant.ApiConstant.DELETE_PATH;
import static platform.core.common.service.persistence.constant.ApiConstant.DETAIL_PATH;
import static platform.core.common.service.persistence.constant.ApiConstant.FETCH_PATH;
import static platform.core.common.service.persistence.constant.ApiConstant.MERCHANT_SERVICE;
import static platform.core.common.service.persistence.constant.ApiConstant.ROUTES_PATH;
import static platform.core.common.service.persistence.constant.ApiConstant.UPDATE_PATH;


@RestController
@RequestMapping(API_PATH + API_VERSION + MERCHANT_SERVICE + ROUTES_PATH)
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('route:management') or hasRole('MERCHANT_OWNER')")
public class MerchantRouteController {

    private final RouteManagementService routeManagementService;
    private final ApiResultFactory apiResultFactory;
    private final RouteResponseMapper routeResponseMapper;

    private final SystemLog sLog = SystemLog.getLogger(this.getClass());

    @InitBinder
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.setDisallowedFields("requestId", "requestDateTime", "channel", "data");
    }

    @GetMapping(FETCH_PATH + DETAIL_PATH)
    public ResponseEntity<FetchDetailRouteResponse> fetchDetailRoute(
            HttpServletRequest servletRequest,
            @RequestParam String routeId,
            @RequestParam String merchantId
    ) {

        BaseRequest baseRequest = ApiRequestUtils.getBaseRequestOrDefault(servletRequest);

        FetchDetailRouteResult result =
                routeManagementService.fetchDetailRoute(
                        FetchDetailRouteQuery.builder()
                                .context(HttpUtils.toContext(baseRequest))
                                .routeId(routeId)
                                .merchantId(merchantId)
                                .build()
                );


        FetchDetailRouteResponse response = FetchDetailRouteResponse.builder()
                .requestId(baseRequest.getRequestId())
                .requestDateTime(baseRequest.getRequestDateTime())
                .channel(baseRequest.getChannel())
                .data(FetchDetailRouteResponse.FetchDetailRouteResponseData
                        .builder()
                        .id(result.id())
                        .creator(result.creator())
                        .originCode(result.originCode())
                        .originName(result.originName())
                        .destinationCode(result.destinationCode())
                        .destinationName(result.destinationName())
                        .originDepartmentId(result.originDepartmentId())
                        .originDepartmentName(result.originDepartmentName())
                        .destinationDepartmentId(result.destinationDepartmentId())
                        .destinationDepartmentName(result.destinationDepartmentName())
                        .duration(result.duration())
                        .status(result.status())
                        .build())
                .build();

        if (result.routePoints() != null) {
            List<SearchRouteResponse.SearchRoutePoints> routePoints = result.routePoints().stream()
                    .map(point -> SearchRouteResponse.SearchRoutePoints.builder()
                            .id(point.id())
                            .routeId(point.routeId())
                            .creator(point.creator())
                            .stopOrder(point.stopOrder())
                            .note(point.note())
                            .departmentId(point.departmentId())
                            .stopName(point.stopName())
                            .stopAddress(point.stopAddress())
                            .stopCity(point.stopCity())
                            .stopLatitude(point.stopLatitude())
                            .stopLongitude(point.stopLongitude())
                            .stayDuration(point.stayDuration())
                            .timeAtDepartment(point.timeAtDepartment())
                            .createdAt(point.createdAt())
                            .createdBy(point.createdBy())
                            .build())
                    .collect(Collectors.toList());


            response.getData().setRoutePoints(routePoints);
        }
        sLog.info("[ROUTE-DETAIL] Fetch Route Detail Response: {}", response);

        return HttpUtils.buildResponse(baseRequest, response);
    }

    @GetMapping(FETCH_PATH)
    public ResponseEntity<FetchRouteResponse> fetchRoutes(@RequestParam(defaultValue = "1") int pageNumber,
                                                          @RequestParam(defaultValue = "10") int pageSize,
                                                          @RequestParam(required = false) RouteStatus status,
                                                          HttpServletRequest servletRequest) {
        BaseRequest baseRequest = ApiRequestUtils.getBaseRequestOrDefault(servletRequest);
        String merchantId = ApiRequestUtils.requireMerchantId(servletRequest, baseRequest);

        FetchRoutesResult result = routeManagementService.fetchRoutes(FetchRoutesQuery.builder()
                .context(HttpUtils.toContext(baseRequest, merchantId))
                .merchantId(merchantId)
                .status(status)
                .pageNumber(String.valueOf(pageNumber))
                .pageSize(String.valueOf(pageSize))
                .build());

        FetchRouteResponse response = FetchRouteResponse.builder()
                .requestId(baseRequest.getRequestId())
                .requestDateTime(baseRequest.getRequestDateTime())
                .channel(baseRequest.getChannel())
                .result(apiResultFactory.buildSuccess())
                .data(FetchRouteResponse.FetchRouteResponsePage.builder()
                        .items(result.items().stream()
                                .map(routeResponseMapper::toFetchRouteResponseData)
                                .toList())
                        .pagination(FetchRouteResponse.Pagination.builder()
                                .pageNumber(result.pageNumber())
                                .pageSize(result.pageSize())
                                .totalElements(result.totalElements())
                                .totalPages(result.totalPages())
                                .build())
                        .build())
                .build();

        return HttpUtils.buildResponse(baseRequest, response);
    }

    @PostMapping(UPDATE_PATH)
    public ResponseEntity<UpdateRouteResponse> updateRoute(@Valid @RequestBody UpdateRouteRequest request,
                                                           HttpServletRequest servletRequest) {
        sLog.info("[UPDATE-ROUTE] Update Route Request: {}", request);
        String merchantId = ApiRequestUtils.requireMerchantId(servletRequest, request);
        List<UpdateRouteCommand.UpdateRoutePointCommand> routePointCommandList = null;
        if (request.getData().getRoutePoints() != null) {
            routePointCommandList = request.getData().getRoutePoints().stream().map(
                    point -> UpdateRouteCommand.UpdateRoutePointCommand.builder()
                            .stopOrder(point.getStopOrder())
                            .note(point.getNote())
                            .departmentId(point.getDepartmentId())
                            .stopName(point.getStopName())
                            .stopAddress(point.getStopAddress())
                            .stopCity(point.getStopCity())
                            .stopLatitude(point.getStopLatitude())
                            .stopLongitude(point.getStopLongitude())
                            .timeAtDepartment(point.getTimeAtDepartment())
                            .build()
            ).toList();
        }

        UpdateRouteResult result = routeManagementService.updateRoute(UpdateRouteCommand.builder()
                .context(HttpUtils.toContext(request, merchantId))
                .routeId(request.getRouteId())
                .creator(request.getCreator())
                .originName(request.getData().getOriginName())
                .originDepartmentId(request.getData().getOriginDepartmentId())
                .destinationDepartmentId(request.getData().getDestinationDepartmentId())
                .destinationName(request.getData().getDestinationName())
                .status(RouteStatus.valueOf(request.getData().getStatus()))
                .duration(request.getData().getDuration())
                .routePoints(routePointCommandList)
                .build());

        UpdateRouteResponse response = UpdateRouteResponse.builder()
                .routeId(result.routeId())
                .creator(result.creator())
                .data(UpdateRouteResponse.UpdateRouteResponseData.builder()
                        .originCode(result.originCode())
                        .originName(result.originName())
                        .destinationCode(result.destinationCode())
                        .destinationName(result.destinationName())
                        .originDepartmentId(result.originDepartmentId())
                        .destinationDepartmentId(result.destinationDepartmentId())
                        .status(result.status())
                        .duration(result.duration())
                        .routePoints(result.routePoints() == null ? null : result.routePoints().stream().map(
                                point -> UpdateRouteResponse.UpdateRoutePointResponse.builder()
                                        .stopOrder(point.stopOrder())
                                        .note(point.note())
                                        .departmentId(point.departmentId())
                                        .stopName(point.stopName())
                                        .stopAddress(point.stopAddress())
                                        .stopCity(point.stopCity())
                                        .stopLatitude(point.stopLatitude())
                                        .stopLongitude(point.stopLongitude())
                                        .timeAtDepartment(point.timeAtDepartment())
                                        .build()
                        ).collect(Collectors.toList()))
                        .build())
                .build();

        sLog.info("[UPDATE-ROUTE] Update Route Response: {}", response);
        return HttpUtils.buildResponse(request, response);
    }

    @PostMapping(CREATE_PATH)
    public ResponseEntity<CreateRouteResponse> createRoute(@Valid @RequestBody CreateRouteRequest request,
                                                           HttpServletRequest servletRequest) {
        sLog.info("[CREATE-ROUTE] Create Route Request: {}", request);
        String merchantId = ApiRequestUtils.requireMerchantId(servletRequest, request);
        List<RoutePointCommand> routePointCommands = new ArrayList<>();
        if (request.getData().getRoutePoints() != null) {
            routePointCommands = request.getData().getRoutePoints().stream()
                    .map(point -> RoutePointCommand.builder()
                            .stopOrder(point.getStopOrder())
                            .note(point.getNote())
                            .departmentId(point.getDepartmentId())
                            .stopName(point.getStopName())
                            .stopAddress(point.getStopAddress())
                            .stopCity(point.getStopCity())
                            .stopLatitude(point.getStopLatitude())
                            .stopLongitude(point.getStopLongitude())
                            .timeAtDepartment(point.getTimeAtDepartment())
                            .build())
                    .toList();
        }

        CreateRouteResult result = routeManagementService.createRoute(CreateRouteCommand.builder()
                .context(HttpUtils.toContext(request, merchantId))
                .merchantId(merchantId)
                .creator(request.getData().getCreator())
                .originName(request.getData().getOriginName())
                .destinationName(request.getData().getDestinationName())
                .originDepartmentId(request.getData().getOriginDepartmentId())
                .destinationDepartmentId(request.getData().getDestinationDepartmentId())
                .duration(request.getData().getDuration())
                .routePoints(routePointCommands)
                .build());


        List<CreateRouteRequest.RoutePoints> routePointResponses = new ArrayList<>();
        if (result.routePoints() != null) {
            routePointResponses = result.routePoints().stream()
                    .map(point -> {
                        return CreateRouteRequest.RoutePoints.builder()
                                .stopOrder(point.stopOrder())
                                .note(point.note())
                                .departmentId(point.departmentId())
                                .stopName(point.stopName())
                                .stopAddress(point.stopAddress())
                                .stopCity(point.stopCity())
                                .stopLatitude(point.stopLatitude())
                                .stopLongitude(point.stopLongitude())
                                .timeAtDepartment(point.timeAtDepartment())
                                .build();
                    })
                    .collect(Collectors.toList());
        }

        CreateRouteResponse response = CreateRouteResponse.builder()
                .requestId(request.getRequestId())
                .requestDateTime(request.getRequestDateTime())
                .channel(request.getChannel())
                .result(apiResultFactory.buildSuccess())
                .data(CreateRouteResponse.CreateRouteResponseData.builder()
                        .id(result.id())
                        .creator(result.creator())
                        .originName(result.originName())
                        .originCode(result.originCode())
                        .originDepartmentId(result.originDepartmentId())
                        .originDepartmentName(result.originDepartmentName())
                        .destinationCode(result.destinationCode())
                        .destinationName(result.destinationName())
                        .destinationDepartmentId(result.destinationDepartmentId())
                        .destinationDepartmentName(result.destinationDepartmentName())
                        .status(result.status())
                        .duration(result.duration())
                        .routePoints(routePointResponses)
                        .build())
                .build();

        sLog.info("[CREATE-ROUTE] Create Route Response: {}", response);
        return HttpUtils.buildResponse(request, response);
    }

    @PostMapping(DELETE_PATH)
    public ResponseEntity<DeleteRouteResponse> deleteRoute(@Valid @RequestBody DeleteRouteRequest request,
                                                           HttpServletRequest servletRequest) {
        sLog.info("[DELETE-ROUTE] Delete Route Request: {}", request);
        String merchantId = ApiRequestUtils.requireMerchantId(servletRequest, request);
        DeleteRouteResult result = routeManagementService.deleteRoute(DeleteRouteCommand.builder()
                .context(HttpUtils.toContext(request, merchantId))
                .creator(request.getData().getCreator())
                .routeId(request.getData().getRouteId())
                .merchantId(merchantId)
                .build());


        DeleteRouteResponse response = DeleteRouteResponse.builder()
                .result(apiResultFactory.buildSuccess())
                .data(DeleteRouteResponse.DeleteRouteResponseData.builder()
                        .creator(result.creator())
                        .routeId(result.routeId())
                        .status(result.status())
                        .updatedAt(result.updatedAt())
                        .build())
                .build();

        sLog.info("[DELETE-ROUTE] Delete Route Response: {}", response);
        return HttpUtils.buildResponse(request, response);
    }
}
