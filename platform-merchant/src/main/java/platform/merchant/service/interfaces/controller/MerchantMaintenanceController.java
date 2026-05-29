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
import platform.merchant.service.application.command.maintenance.CreateMaintenancePlanCommand;
import platform.merchant.service.application.command.maintenance.CreateMaintenancePlanResult;
import platform.merchant.service.application.command.maintenance.DeleteMaintenancePlanCommand;
import platform.merchant.service.application.command.maintenance.DeleteMaintenancePlanResult;
import platform.merchant.service.application.command.maintenance.FetchMaintenancePlanDetailQuery;
import platform.merchant.service.application.command.maintenance.FetchMaintenancePlanDetailResult;
import platform.merchant.service.application.command.maintenance.FetchMaintenancePlansQuery;
import platform.merchant.service.application.command.maintenance.FetchMaintenancePlansResult;
import platform.merchant.service.application.command.maintenance.UpdateMaintenancePlanCommand;
import platform.merchant.service.application.command.maintenance.UpdateMaintenancePlanResult;
import platform.merchant.service.application.service.MaintenancePlanManagementService;
import platform.merchant.service.domain.maintenance.MaintenancePlanStatus;
import platform.merchant.service.domain.maintenance.MaintenancePlanType;
import platform.core.common.service.persistence.utils.ApiRequestUtils;
import platform.merchant.service.infrastructure.persistence.utils.HttpUtils;
import platform.merchant.service.interfaces.factory.ApiResultFactory;
import platform.core.common.service.api.BaseRequest;
import platform.merchant.service.interfaces.model.maintenance.CreateMaintenancePlanRequest;
import platform.merchant.service.interfaces.model.maintenance.CreateMaintenancePlanResponse;
import platform.merchant.service.interfaces.model.maintenance.DeleteMaintenancePlanRequest;
import platform.merchant.service.interfaces.model.maintenance.DeleteMaintenancePlanResponse;
import platform.merchant.service.interfaces.model.maintenance.FetchMaintenancePlanDetailResponse;
import platform.merchant.service.interfaces.model.maintenance.FetchMaintenancePlanResponse;
import platform.merchant.service.interfaces.model.maintenance.UpdateMaintenancePlanRequest;
import platform.merchant.service.interfaces.model.maintenance.UpdateMaintenancePlanResponse;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static platform.core.common.service.persistence.constant.ApiConstant.API_PATH;
import static platform.core.common.service.persistence.constant.ApiConstant.API_VERSION;
import static platform.core.common.service.persistence.constant.ApiConstant.CREATE_PATH;
import static platform.core.common.service.persistence.constant.ApiConstant.DELETE_PATH;
import static platform.core.common.service.persistence.constant.ApiConstant.DETAIL_PATH;
import static platform.core.common.service.persistence.constant.ApiConstant.FETCH_PATH;
import static platform.core.common.service.persistence.constant.ApiConstant.MAINTENANCE_PLAN_PATH;
import static platform.core.common.service.persistence.constant.ApiConstant.MERCHANT_SERVICE;
import static platform.core.common.service.persistence.constant.ApiConstant.UPDATE_PATH;

@RestController
@RequestMapping(API_PATH + API_VERSION + MERCHANT_SERVICE)
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('vehicle:management') or hasRole('MERCHANT_OWNER')")
public class MerchantMaintenanceController {

    private final MaintenancePlanManagementService maintenancePlanManagementService;
    private final ApiResultFactory apiResultFactory;
    private final SystemLog sLog = SystemLog.getLogger(this.getClass());

    @InitBinder
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.setDisallowedFields("requestId", "requestDateTime", "channel", "data");
    }

    @PostMapping(MAINTENANCE_PLAN_PATH + CREATE_PATH)
    public ResponseEntity<CreateMaintenancePlanResponse> createMaintenancePlan(
            HttpServletRequest servletRequest,
            @Valid @RequestBody CreateMaintenancePlanRequest request
    ) {
        String merchantId = ApiRequestUtils.requireMerchantId(servletRequest, request);
        sLog.info("[MAINTENANCE-PLAN] Create Maintenance Plan Request: {}", request);

        CreateMaintenancePlanResult result = maintenancePlanManagementService.createMaintenancePlan(
                CreateMaintenancePlanCommand.builder()
                        .context(HttpUtils.toContext(request, merchantId))
                        .merchantId(merchantId)
                        .creator(request.getData().getCreator())
                        .vehicleId(request.getData().getVehicleId())
                        .code(request.getData().getCode())
                        .title(request.getData().getTitle())
                        .description(request.getData().getDescription())
                        .type(request.getData().getType())
                        .plannedDate(request.getData().getPlannedDate())
                        .dueDate(request.getData().getDueDate())
                        .currentOdometerKm(request.getData().getCurrentOdometerKm())
                        .targetOdometerKm(request.getData().getTargetOdometerKm())
                        .estimatedCost(request.getData().getEstimatedCost())
                        .serviceProvider(request.getData().getServiceProvider())
                        .note(request.getData().getNote())
                        .build()
        );

        CreateMaintenancePlanResponse response = CreateMaintenancePlanResponse.builder()
                .result(apiResultFactory.buildSuccess())
                .data(CreateMaintenancePlanResponse.CreateMaintenancePlanResponseData.builder()
                        .id(result.id())
                        .merchantId(result.merchantId())
                        .vehicleId(result.vehicleId())
                        .code(result.code())
                        .title(result.title())
                        .description(result.description())
                        .type(result.type())
                        .status(result.status())
                        .plannedDate(result.plannedDate())
                        .dueDate(result.dueDate())
                        .completedDate(result.completedDate())
                        .currentOdometerKm(result.currentOdometerKm())
                        .targetOdometerKm(result.targetOdometerKm())
                        .estimatedCost(result.estimatedCost())
                        .actualCost(result.actualCost())
                        .serviceProvider(result.serviceProvider())
                        .note(result.note())
                        .build())
                .build();

        return HttpUtils.buildResponse(request, response);
    }

    @PostMapping(MAINTENANCE_PLAN_PATH + UPDATE_PATH)
    public ResponseEntity<UpdateMaintenancePlanResponse> updateMaintenancePlan(
            HttpServletRequest servletRequest,
            @Valid @RequestBody UpdateMaintenancePlanRequest request
    ) {
        String merchantId = ApiRequestUtils.requireMerchantId(servletRequest, request);
        sLog.info("[MAINTENANCE-PLAN] Update Maintenance Plan Request: {}", request);

        UpdateMaintenancePlanResult result = maintenancePlanManagementService.updateMaintenancePlan(
                UpdateMaintenancePlanCommand.builder()
                        .context(HttpUtils.toContext(request, merchantId))
                        .merchantId(merchantId)
                        .creator(request.getData().getCreator())
                        .maintenancePlanId(request.getData().getMaintenancePlanId())
                        .vehicleId(request.getData().getVehicleId())
                        .code(request.getData().getCode())
                        .title(request.getData().getTitle())
                        .description(request.getData().getDescription())
                        .type(request.getData().getType())
                        .status(request.getData().getStatus())
                        .plannedDate(request.getData().getPlannedDate())
                        .dueDate(request.getData().getDueDate())
                        .completedDate(request.getData().getCompletedDate())
                        .currentOdometerKm(request.getData().getCurrentOdometerKm())
                        .targetOdometerKm(request.getData().getTargetOdometerKm())
                        .estimatedCost(request.getData().getEstimatedCost())
                        .actualCost(request.getData().getActualCost())
                        .serviceProvider(request.getData().getServiceProvider())
                        .note(request.getData().getNote())
                        .build()
        );

        UpdateMaintenancePlanResponse response = UpdateMaintenancePlanResponse.builder()
                .result(apiResultFactory.buildSuccess())
                .data(UpdateMaintenancePlanResponse.UpdateMaintenancePlanResponseData.builder()
                        .id(result.id())
                        .merchantId(result.merchantId())
                        .vehicleId(result.vehicleId())
                        .code(result.code())
                        .title(result.title())
                        .description(result.description())
                        .type(result.type())
                        .status(result.status())
                        .plannedDate(result.plannedDate())
                        .dueDate(result.dueDate())
                        .completedDate(result.completedDate())
                        .currentOdometerKm(result.currentOdometerKm())
                        .targetOdometerKm(result.targetOdometerKm())
                        .estimatedCost(result.estimatedCost())
                        .actualCost(result.actualCost())
                        .serviceProvider(result.serviceProvider())
                        .note(result.note())
                        .build())
                .build();

        return HttpUtils.buildResponse(request, response);
    }

    @PostMapping(MAINTENANCE_PLAN_PATH + DELETE_PATH)
    public ResponseEntity<DeleteMaintenancePlanResponse> deleteMaintenancePlan(
            HttpServletRequest servletRequest,
            @Valid @RequestBody DeleteMaintenancePlanRequest request
    ) {
        String merchantId = ApiRequestUtils.requireMerchantId(servletRequest, request);
        sLog.info("[MAINTENANCE-PLAN] Delete Maintenance Plan Request: {}", request);

        DeleteMaintenancePlanResult result = maintenancePlanManagementService.deleteMaintenancePlan(
                DeleteMaintenancePlanCommand.builder()
                        .context(HttpUtils.toContext(request, merchantId))
                        .merchantId(merchantId)
                        .creator(request.getData().getCreator())
                        .maintenancePlanId(request.getData().getMaintenancePlanId())
                        .build()
        );

        DeleteMaintenancePlanResponse response = DeleteMaintenancePlanResponse.builder()
                .result(apiResultFactory.buildSuccess())
                .data(DeleteMaintenancePlanResponse.DeleteMaintenancePlanResponseData.builder()
                        .id(result.id())
                        .code(result.code())
                        .status(result.status())
                        .build())
                .build();

        return HttpUtils.buildResponse(request, response);
    }

    @GetMapping(MAINTENANCE_PLAN_PATH + FETCH_PATH)
    public ResponseEntity<FetchMaintenancePlanResponse> fetchMaintenancePlans(
            HttpServletRequest servletRequest,
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String vehicleId,
            @RequestParam(required = false) MaintenancePlanStatus status,
            @RequestParam(required = false) MaintenancePlanType type,
            @RequestParam(required = false) LocalDate fromPlannedDate,
            @RequestParam(required = false) LocalDate toPlannedDate
    ) {
        BaseRequest baseRequest = ApiRequestUtils.getBaseRequestOrDefault(servletRequest);
        String merchantId = ApiRequestUtils.requireMerchantId(servletRequest, baseRequest);

        FetchMaintenancePlansResult result = maintenancePlanManagementService.fetchMaintenancePlans(
                FetchMaintenancePlansQuery.builder()
                        .context(HttpUtils.toContext(baseRequest, merchantId))
                        .merchantId(merchantId)
                        .pageNumber(String.valueOf(pageNumber))
                        .pageSize(String.valueOf(pageSize))
                        .vehicleId(vehicleId)
                        .status(status)
                        .type(type)
                        .fromPlannedDate(fromPlannedDate)
                        .toPlannedDate(toPlannedDate)
                        .build()
        );

        List<FetchMaintenancePlanResponse.FetchMaintenancePlanResponseData> items = result.items().stream()
                .map(item -> FetchMaintenancePlanResponse.FetchMaintenancePlanResponseData.builder()
                        .id(item.id())
                        .vehicle(FetchMaintenancePlanResponse.MaintenancePlanVehicleResponseData.builder()
                                .id(item.vehicle().id())
                                .templateId(item.vehicle().templateId())
                                .status(item.vehicle().status())
                                .category(item.vehicle().category())
                                .type(item.vehicle().type())
                                .vehiclePlate(item.vehicle().vehiclePlate())
                                .seatCapacity(item.vehicle().seatCapacity())
                                .hasFloor(item.vehicle().hasFloor())
                                .manufacturer(item.vehicle().manufacturer())
                                .build())
                        .code(item.code())
                        .title(item.title())
                        .type(item.type())
                        .status(item.status())
                        .plannedDate(item.plannedDate())
                        .dueDate(item.dueDate())
                        .targetOdometerKm(item.targetOdometerKm())
                        .estimatedCost(item.estimatedCost())
                        .serviceProvider(item.serviceProvider())
                        .build())
                .collect(Collectors.toList());

        FetchMaintenancePlanResponse response = FetchMaintenancePlanResponse.builder()
                .requestId(baseRequest.getRequestId())
                .requestDateTime(baseRequest.getRequestDateTime())
                .channel(baseRequest.getChannel())
                .result(apiResultFactory.buildSuccess())
                .data(FetchMaintenancePlanResponse.FetchMaintenancePlanResponsePage.builder()
                        .items(items)
                        .pagination(FetchMaintenancePlanResponse.Pagination.builder()
                                .pageNumber(result.pageNumber())
                                .pageSize(result.pageSize())
                                .totalElements(result.totalElements())
                                .totalPages(result.totalPages())
                                .build())
                        .build())
                .build();

        return HttpUtils.buildResponse(baseRequest, response);
    }

    @GetMapping(MAINTENANCE_PLAN_PATH + DETAIL_PATH)
    public ResponseEntity<FetchMaintenancePlanDetailResponse> fetchMaintenancePlanDetail(
            HttpServletRequest servletRequest,
            @RequestParam String maintenancePlanId
    ) {
        BaseRequest baseRequest = ApiRequestUtils.getBaseRequestOrDefault(servletRequest);
        String merchantId = ApiRequestUtils.requireMerchantId(servletRequest, baseRequest);

        FetchMaintenancePlanDetailResult result = maintenancePlanManagementService.fetchMaintenancePlanDetail(
                FetchMaintenancePlanDetailQuery.builder()
                        .context(HttpUtils.toContext(baseRequest, merchantId))
                        .merchantId(merchantId)
                        .maintenancePlanId(maintenancePlanId)
                        .build()
        );

        FetchMaintenancePlanDetailResponse response = FetchMaintenancePlanDetailResponse.builder()
                .requestId(baseRequest.getRequestId())
                .requestDateTime(baseRequest.getRequestDateTime())
                .channel(baseRequest.getChannel())
                .result(apiResultFactory.buildSuccess())
                .data(FetchMaintenancePlanDetailResponse.FetchMaintenancePlanDetailResponseData.builder()
                        .id(result.id())
                        .merchantId(result.merchantId())
                        .vehicle(FetchMaintenancePlanDetailResponse.MaintenancePlanVehicleDetailResponseData.builder()
                                .id(result.vehicle().id())
                                .templateId(result.vehicle().templateId())
                                .status(result.vehicle().status())
                                .category(result.vehicle().category())
                                .type(result.vehicle().type())
                                .vehiclePlate(result.vehicle().vehiclePlate())
                                .seatCapacity(result.vehicle().seatCapacity())
                                .hasFloor(result.vehicle().hasFloor())
                                .manufacturer(result.vehicle().manufacturer())
                                .build())
                        .code(result.code())
                        .title(result.title())
                        .description(result.description())
                        .type(result.type())
                        .status(result.status())
                        .plannedDate(result.plannedDate())
                        .dueDate(result.dueDate())
                        .completedDate(result.completedDate())
                        .currentOdometerKm(result.currentOdometerKm())
                        .targetOdometerKm(result.targetOdometerKm())
                        .estimatedCost(result.estimatedCost())
                        .actualCost(result.actualCost())
                        .serviceProvider(result.serviceProvider())
                        .note(result.note())
                        .build())
                .build();

        return HttpUtils.buildResponse(baseRequest, response);
    }
}
