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
import platform.core.common.service.api.ApiResult;
import platform.core.common.service.api.BaseRequest;
import platform.core.common.service.domain.vehicle.VehicleStatus;
import platform.core.common.service.persistence.utils.ApiRequestUtils;
import platform.merchant.service.application.command.vehicle.AddVehicleCommand;
import platform.merchant.service.application.command.vehicle.AddVehicleResult;
import platform.merchant.service.application.command.vehicle.DeleteVehicleCommand;
import platform.merchant.service.application.command.vehicle.DeleteVehicleResult;
import platform.merchant.service.application.command.vehicle.FetchVehicleDetailQuery;
import platform.merchant.service.application.command.vehicle.FetchVehicleDetailResult;
import platform.merchant.service.application.command.vehicle.FetchVehiclesQuery;
import platform.merchant.service.application.command.vehicle.FetchVehiclesResult;
import platform.merchant.service.application.command.vehicle.UpdateVehicleCommand;
import platform.merchant.service.application.command.vehicle.UpdateVehicleResult;
import platform.merchant.service.application.service.VehicleManagementService;
import platform.merchant.service.infrastructure.persistence.utils.HttpUtils;
import platform.merchant.service.interfaces.factory.ApiResultFactory;
import platform.merchant.service.interfaces.model.vehicle.AddVehicleRequest;
import platform.merchant.service.interfaces.model.vehicle.AddVehicleResponse;
import platform.merchant.service.interfaces.model.vehicle.DeleteVehicleRequest;
import platform.merchant.service.interfaces.model.vehicle.DeleteVehicleResponse;
import platform.merchant.service.interfaces.model.vehicle.FetchVehicleDetailResponse;
import platform.merchant.service.interfaces.model.vehicle.FetchVehicleResponse;
import platform.merchant.service.interfaces.model.vehicle.UpdateVehicleRequest;
import platform.merchant.service.interfaces.model.vehicle.UpdateVehicleResponse;
import vn.com.go.routex.identity.security.log.SystemLog;

import java.util.List;
import java.util.stream.Collectors;

import static platform.core.common.service.persistence.constant.ApiConstant.API_PATH;
import static platform.core.common.service.persistence.constant.ApiConstant.API_VERSION;
import static platform.core.common.service.persistence.constant.ApiConstant.CREATE_PATH;
import static platform.core.common.service.persistence.constant.ApiConstant.DELETE_PATH;
import static platform.core.common.service.persistence.constant.ApiConstant.DETAIL_PATH;
import static platform.core.common.service.persistence.constant.ApiConstant.FETCH_PATH;
import static platform.core.common.service.persistence.constant.ApiConstant.MERCHANT_SERVICE;
import static platform.core.common.service.persistence.constant.ApiConstant.UPDATE_PATH;
import static platform.core.common.service.persistence.constant.ApiConstant.VEHICLE_PATH;

@RestController
@RequestMapping(API_PATH + API_VERSION + MERCHANT_SERVICE)
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('vehicle:management') or hasRole('MERCHANT_OWNER')")
public class MerchantVehicleController {

    private final VehicleManagementService vehicleManagementService;
    private final ApiResultFactory apiResultFactory;
    private final SystemLog sLog = SystemLog.getLogger(this.getClass());

    @InitBinder
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.setDisallowedFields("requestId", "requestDateTime", "channel", "data");
    }


    @PostMapping(VEHICLE_PATH + CREATE_PATH)
    public ResponseEntity<AddVehicleResponse> addVehicle(@Valid @RequestBody AddVehicleRequest request,
                                                         HttpServletRequest servletRequest) {
        sLog.info("[VEHICLE-MANAGEMENT] Add Vehicle Request: {}", request);
        String merchantId = ApiRequestUtils.requireMerchantId(servletRequest, request);
        AddVehicleResult result = vehicleManagementService.addVehicle(AddVehicleCommand.builder()
                .context(HttpUtils.toContext(request, merchantId))
                .merchantId(merchantId)
                .creator(request.getData().getCreator())
                .templateId(request.getData().getTemplateId())
                .vehiclePlate(request.getData().getVehiclePlate())
                .build());


        AddVehicleResponse response = AddVehicleResponse.builder()
                .result(ApiResult.buildSuccess())
                .data(AddVehicleResponse.AddVehicleResponseData.builder()
                        .id(result.id())
                        .templateId(result.templateId())
                        .creator(result.creator())
                        .category(result.category())
                        .type(result.type())
                        .vehiclePlate(result.vehiclePlate())
                        .seatCapacity(result.seatCapacity())
                        .manufacturer(result.manufacturer())
                        .status(result.status())
                        .build())
                .build();

        sLog.info("[VEHICLE-MANAGEMENT] Add Vehicle Response: {}", response);
        return HttpUtils.buildResponse(request, response);
    }

    @PostMapping(VEHICLE_PATH + UPDATE_PATH)
    public ResponseEntity<UpdateVehicleResponse> updateVehicle(@Valid @RequestBody UpdateVehicleRequest request,
                                                               HttpServletRequest servletRequest) {
        sLog.info("[VEHICLE-MANAGEMENT] Update Vehicle Request: {}", request);
        String merchantId = ApiRequestUtils.requireMerchantId(servletRequest, request);
        UpdateVehicleResult result = vehicleManagementService.updateVehicle(UpdateVehicleCommand.builder()
                .context(HttpUtils.toContext(request, merchantId))
                .merchantId(merchantId)
                .creator(request.getData().getCreator())
                .vehicleId(request.getData().getVehicleId())
                .templateId(request.getData().getTemplateId())
                .vehiclePlate(request.getData().getVehiclePlate())
                .status(request.getData().getStatus())
                .build());


        UpdateVehicleResponse response = UpdateVehicleResponse.builder()
                .result(apiResultFactory.buildSuccess())
                .data(UpdateVehicleResponse.UpdateVehicleResponseData.builder()
                        .id(result.id())
                        .templateId(result.templateId())
                        .creator(result.creator())
                        .category(result.category())
                        .type(result.type())
                        .vehiclePlate(result.vehiclePlate())
                        .seatCapacity(result.seatCapacity())
                        .hasFloor(result.hasFloor())
                        .manufacturer(result.manufacturer())
                        .status(result.status())
                        .build())
                .build();

        sLog.info("[VEHICLE-MANAGEMENT] Update Vehicle Response: {}", response);
        return HttpUtils.buildResponse(request, response);
    }

    @PostMapping(VEHICLE_PATH + DELETE_PATH)
    public ResponseEntity<DeleteVehicleResponse> deleteVehicle(@Valid @RequestBody DeleteVehicleRequest request,
                                                               HttpServletRequest servletRequest) {
        sLog.info("[VEHICLE-MANAGEMENT] Delete Vehicle Request: {}", request);
        String merchantId = ApiRequestUtils.requireMerchantId(servletRequest, request);
        DeleteVehicleResult result = vehicleManagementService.deleteVehicle(DeleteVehicleCommand.builder()
                .context(HttpUtils.toContext(request, merchantId))
                .merchantId(merchantId)
                .creator(request.getData().getCreator())
                .vehicleId(request.getData().getVehicleId())
                .build());


        DeleteVehicleResponse response = DeleteVehicleResponse.builder()
                .result(apiResultFactory.buildSuccess())
                .data(DeleteVehicleResponse.DeleteVehicleResponseData.builder()
                        .id(result.id())
                        .status(result.status())
                        .build())
                .build();

        sLog.info("[VEHICLE-MANAGEMENT] Delete Vehicle Response: {}", response);
        return HttpUtils.buildResponse(request, response);
    }

    @GetMapping(VEHICLE_PATH + FETCH_PATH)
    public ResponseEntity<FetchVehicleResponse> fetchVehicles(
            HttpServletRequest servletRequest,
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) VehicleStatus status
    ) {
        BaseRequest baseRequest = ApiRequestUtils.getBaseRequestOrDefault(servletRequest);
        String merchantId = ApiRequestUtils.requireMerchantId(servletRequest, baseRequest);

        FetchVehiclesResult result = vehicleManagementService.fetchVehicles(FetchVehiclesQuery.builder()
                .context(HttpUtils.toContext(baseRequest, merchantId))
                .pageNumber(String.valueOf(pageNumber))
                .pageSize(String.valueOf(pageSize))
                .merchantId(merchantId)
                .status(status)
                .build());

        List<FetchVehicleResponse.FetchVehicleResponseData> items = result.items().stream()
                .map(v -> FetchVehicleResponse.FetchVehicleResponseData.builder()
                        .id(v.id())
                        .templateId(v.templateId())
                        .creator(v.creator())
                        .status(v.status())
                        .category(v.category())
                        .type(v.type())
                        .vehiclePlate(v.vehiclePlate())
                        .seatCapacity(v.seatCapacity())
                        .hasFloor(v.hasFloor())
                        .manufacturer(v.manufacturer())
                        .build())
                .collect(Collectors.toList());

        FetchVehicleResponse response = FetchVehicleResponse.builder()
                .requestId(baseRequest.getRequestId())
                .requestDateTime(baseRequest.getRequestDateTime())
                .channel(baseRequest.getChannel())
                .result(apiResultFactory.buildSuccess())
                .data(FetchVehicleResponse.FetchVehicleResponsePage.builder()
                        .items(items)
                        .pagination(FetchVehicleResponse.Pagination.builder()
                                .pageNumber(result.pageNumber())
                                .pageSize(result.pageSize())
                                .totalElements(result.totalElements())
                                .totalPages(result.totalPages())
                                .build())
                        .build())
                .build();

        return HttpUtils.buildResponse(baseRequest, response);
    }

    @GetMapping(VEHICLE_PATH + DETAIL_PATH)
    public ResponseEntity<FetchVehicleDetailResponse> fetchVehicleDetail(
            HttpServletRequest servletRequest,
            @RequestParam String vehicleId
    ) {
        BaseRequest baseRequest = ApiRequestUtils.getBaseRequestOrDefault(servletRequest);
        String merchantId = ApiRequestUtils.requireMerchantId(servletRequest, baseRequest);

        FetchVehicleDetailResult result = vehicleManagementService.fetchVehicleDetail(FetchVehicleDetailQuery.builder()
                .context(HttpUtils.toContext(baseRequest, merchantId))
                .merchantId(merchantId)
                .vehicleId(vehicleId)
                .build());

        FetchVehicleDetailResponse response = FetchVehicleDetailResponse.builder()
                .requestId(baseRequest.getRequestId())
                .requestDateTime(baseRequest.getRequestDateTime())
                .channel(baseRequest.getChannel())
                .result(apiResultFactory.buildSuccess())
                .data(FetchVehicleDetailResponse.FetchVehicleDetailResponseData.builder()
                        .id(result.id())
                        .merchantId(result.merchantId())
                        .templateId(result.templateId())
                        .creator(result.creator())
                        .status(result.status())
                        .category(result.category())
                        .type(result.type())
                        .vehiclePlate(result.vehiclePlate())
                        .seatCapacity(result.seatCapacity())
                        .hasFloor(result.hasFloor())
                        .manufacturer(result.manufacturer())
                        .build())
                .build();

        return HttpUtils.buildResponse(baseRequest, response);
    }
}
