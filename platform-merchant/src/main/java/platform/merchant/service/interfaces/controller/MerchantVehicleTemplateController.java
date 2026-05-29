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
import platform.merchant.service.application.command.vehicletemplate.CreateVehicleTemplateCommand;
import platform.merchant.service.application.command.vehicletemplate.CreateVehicleTemplateResult;
import platform.merchant.service.application.command.vehicletemplate.DeleteVehicleTemplateCommand;
import platform.merchant.service.application.command.vehicletemplate.DeleteVehicleTemplateResult;
import platform.merchant.service.application.command.vehicletemplate.FetchVehicleTemplateDetailQuery;
import platform.merchant.service.application.command.vehicletemplate.FetchVehicleTemplateDetailResult;
import platform.merchant.service.application.command.vehicletemplate.FetchVehicleTemplatesQuery;
import platform.merchant.service.application.command.vehicletemplate.FetchVehicleTemplatesResult;
import platform.merchant.service.application.command.vehicletemplate.UpdateVehicleTemplateCommand;
import platform.merchant.service.application.command.vehicletemplate.UpdateVehicleTemplateResult;
import platform.merchant.service.application.service.VehicleTemplateManagementService;
import platform.core.common.service.domain.vehicle.VehicleTemplateCategory;
import platform.core.common.service.domain.vehicle.VehicleTemplateStatus;
import platform.core.common.service.domain.vehicle.VehicleTemplateType;
import platform.core.common.service.persistence.utils.ApiRequestUtils;
import platform.merchant.service.infrastructure.persistence.utils.HttpUtils;
import platform.merchant.service.interfaces.factory.ApiResultFactory;
import platform.core.common.service.api.BaseRequest;
import platform.merchant.service.interfaces.model.vehicletemplate.CreateVehicleTemplateRequest;
import platform.merchant.service.interfaces.model.vehicletemplate.CreateVehicleTemplateResponse;
import platform.merchant.service.interfaces.model.vehicletemplate.DeleteVehicleTemplateRequest;
import platform.merchant.service.interfaces.model.vehicletemplate.DeleteVehicleTemplateResponse;
import platform.merchant.service.interfaces.model.vehicletemplate.FetchVehicleTemplateDetailResponse;
import platform.merchant.service.interfaces.model.vehicletemplate.FetchVehicleTemplateResponse;
import platform.merchant.service.interfaces.model.vehicletemplate.UpdateVehicleTemplateRequest;
import platform.merchant.service.interfaces.model.vehicletemplate.UpdateVehicleTemplateResponse;

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
import static platform.core.common.service.persistence.constant.ApiConstant.VEHICLE_TEMPLATE_PATH;

@RestController
@RequestMapping(API_PATH + API_VERSION + MERCHANT_SERVICE)
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('vehicle:management') or hasRole('MERCHANT_OWNER')")
public class MerchantVehicleTemplateController {

    private final VehicleTemplateManagementService vehicleTemplateManagementService;
    private final ApiResultFactory apiResultFactory;
    private final SystemLog sLog = SystemLog.getLogger(this.getClass());

    @InitBinder
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.setDisallowedFields("requestId", "requestDateTime", "channel", "data");
    }

    @PostMapping(VEHICLE_TEMPLATE_PATH + CREATE_PATH)
    public ResponseEntity<CreateVehicleTemplateResponse> createVehicleTemplate(
            @Valid @RequestBody CreateVehicleTemplateRequest request,
            HttpServletRequest servletRequest
    ) {
        sLog.info("[VEHICLE-TEMPLATE] Create Vehicle Template Request: {}", request);
        String merchantId = ApiRequestUtils.requireMerchantId(servletRequest, request);

        CreateVehicleTemplateResult result = vehicleTemplateManagementService.createVehicleTemplate(CreateVehicleTemplateCommand.builder()
                .context(HttpUtils.toContext(request, merchantId))
                .merchantId(merchantId)
                .creator(request.getData().getCreator())
                .code(request.getData().getCode())
                .name(request.getData().getName())
                .manufacturer(request.getData().getManufacturer())
                .model(request.getData().getModel())
                .seatCapacity(request.getData().getSeatCapacity())
                .category(request.getData().getCategory())
                .type(request.getData().getType())
                .fuelType(request.getData().getFuelType())
                .hasFloor(request.getData().getHasFloor())
                .ticketPrice(request.getData().getTicketPrice())
                .status(request.getData().getStatus())
                .build());

        CreateVehicleTemplateResponse response = CreateVehicleTemplateResponse.builder()
                .result(apiResultFactory.buildSuccess())
                .data(CreateVehicleTemplateResponse.CreateVehicleTemplateResponseData.builder()
                        .id(result.id())
                        .merchantId(result.merchantId())
                        .code(result.code())
                        .name(result.name())
                        .manufacturer(result.manufacturer())
                        .model(result.model())
                        .seatCapacity(result.seatCapacity())
                        .category(result.category())
                        .type(result.type())
                        .fuelType(result.fuelType())
                        .hasFloor(result.hasFloor())
                        .ticketPrice(result.ticketPrice())
                        .status(result.status())
                        .build())
                .build();

        return HttpUtils.buildResponse(request, response);
    }

    @PostMapping(VEHICLE_TEMPLATE_PATH + UPDATE_PATH)
    public ResponseEntity<UpdateVehicleTemplateResponse> updateVehicleTemplate(
            @Valid @RequestBody UpdateVehicleTemplateRequest request,
            HttpServletRequest servletRequest
    ) {
        sLog.info("[VEHICLE-TEMPLATE] Update Vehicle Template Request: {}", request);
        String merchantId = ApiRequestUtils.requireMerchantId(servletRequest, request);

        UpdateVehicleTemplateResult result = vehicleTemplateManagementService.updateVehicleTemplate(UpdateVehicleTemplateCommand.builder()
                .context(HttpUtils.toContext(request, merchantId))
                .merchantId(merchantId)
                .creator(request.getData().getCreator())
                .templateId(request.getData().getTemplateId())
                .code(request.getData().getCode())
                .name(request.getData().getName())
                .manufacturer(request.getData().getManufacturer())
                .model(request.getData().getModel())
                .seatCapacity(request.getData().getSeatCapacity())
                .category(request.getData().getCategory())
                .type(request.getData().getType())
                .fuelType(request.getData().getFuelType())
                .hasFloor(request.getData().getHasFloor())
                .ticketPrice(request.getData().getTicketPrice())
                .status(request.getData().getStatus())
                .build());

        UpdateVehicleTemplateResponse response = UpdateVehicleTemplateResponse.builder()
                .result(apiResultFactory.buildSuccess())
                .data(UpdateVehicleTemplateResponse.UpdateVehicleTemplateResponseData.builder()
                        .id(result.id())
                        .merchantId(result.merchantId())
                        .code(result.code())
                        .name(result.name())
                        .manufacturer(result.manufacturer())
                        .model(result.model())
                        .seatCapacity(result.seatCapacity())
                        .category(result.category())
                        .type(result.type())
                        .fuelType(result.fuelType())
                        .hasFloor(result.hasFloor())
                        .ticketPrice(result.ticketPrice())
                        .status(result.status())
                        .build())
                .build();

        return HttpUtils.buildResponse(request, response);
    }

    @PostMapping(VEHICLE_TEMPLATE_PATH + DELETE_PATH)
    public ResponseEntity<DeleteVehicleTemplateResponse> deleteVehicleTemplate(
            @Valid @RequestBody DeleteVehicleTemplateRequest request,
            HttpServletRequest servletRequest
    ) {
        sLog.info("[VEHICLE-TEMPLATE] Delete Vehicle Template Request: {}", request);
        String merchantId = ApiRequestUtils.requireMerchantId(servletRequest, request);

        DeleteVehicleTemplateResult result = vehicleTemplateManagementService.deleteVehicleTemplate(DeleteVehicleTemplateCommand.builder()
                .context(HttpUtils.toContext(request, merchantId))
                .merchantId(merchantId)
                .creator(request.getData().getCreator())
                .templateId(request.getData().getTemplateId())
                .build());

        DeleteVehicleTemplateResponse response = DeleteVehicleTemplateResponse.builder()
                .result(apiResultFactory.buildSuccess())
                .data(DeleteVehicleTemplateResponse.DeleteVehicleTemplateResponseData.builder()
                        .id(result.id())
                        .code(result.code())
                        .status(result.status())
                        .build())
                .build();

        return HttpUtils.buildResponse(request, response);
    }

    @GetMapping(VEHICLE_TEMPLATE_PATH + FETCH_PATH)
    public ResponseEntity<FetchVehicleTemplateResponse> fetchVehicleTemplates(
            HttpServletRequest servletRequest,
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) VehicleTemplateStatus status,
            @RequestParam(required = false) VehicleTemplateCategory category,
            @RequestParam(required = false) VehicleTemplateType type
    ) {
        BaseRequest baseRequest = ApiRequestUtils.getBaseRequestOrDefault(servletRequest);
        String merchantId = ApiRequestUtils.requireMerchantId(servletRequest, baseRequest);

        FetchVehicleTemplatesResult result = vehicleTemplateManagementService.fetchVehicleTemplates(FetchVehicleTemplatesQuery.builder()
                .context(HttpUtils.toContext(baseRequest, merchantId))
                .merchantId(merchantId)
                .pageNumber(String.valueOf(pageNumber))
                .pageSize(String.valueOf(pageSize))
                .status(status)
                .category(category)
                .type(type)
                .build());

        List<FetchVehicleTemplateResponse.FetchVehicleTemplateResponseData> items = result.items().stream()
                .map(template -> FetchVehicleTemplateResponse.FetchVehicleTemplateResponseData.builder()
                        .id(template.id())
                        .merchantId(template.merchantId())
                        .code(template.code())
                        .name(template.name())
                        .manufacturer(template.manufacturer())
                        .model(template.model())
                        .seatCapacity(template.seatCapacity())
                        .category(template.category())
                        .type(template.type())
                        .fuelType(template.fuelType())
                        .hasFloor(template.hasFloor())
                        .ticketPrice(template.ticketPrice())
                        .status(template.status())
                        .build())
                .collect(Collectors.toList());

        FetchVehicleTemplateResponse response = FetchVehicleTemplateResponse.builder()
                .requestId(baseRequest.getRequestId())
                .requestDateTime(baseRequest.getRequestDateTime())
                .channel(baseRequest.getChannel())
                .result(apiResultFactory.buildSuccess())
                .data(FetchVehicleTemplateResponse.FetchVehicleTemplateResponsePage.builder()
                        .items(items)
                        .pagination(FetchVehicleTemplateResponse.Pagination.builder()
                                .pageNumber(result.pageNumber())
                                .pageSize(result.pageSize())
                                .totalElements(result.totalElements())
                                .totalPages(result.totalPages())
                                .build())
                        .build())
                .build();

        return HttpUtils.buildResponse(baseRequest, response);
    }

    @GetMapping(VEHICLE_TEMPLATE_PATH + DETAIL_PATH)
    public ResponseEntity<FetchVehicleTemplateDetailResponse> fetchVehicleTemplateDetail(
            HttpServletRequest servletRequest,
            @RequestParam String templateId
    ) {
        BaseRequest baseRequest = ApiRequestUtils.getBaseRequestOrDefault(servletRequest);
        String merchantId = ApiRequestUtils.requireMerchantId(servletRequest, baseRequest);

        FetchVehicleTemplateDetailResult result = vehicleTemplateManagementService.fetchVehicleTemplateDetail(FetchVehicleTemplateDetailQuery.builder()
                .context(HttpUtils.toContext(baseRequest, merchantId))
                .merchantId(merchantId)
                .templateId(templateId)
                .build());

        FetchVehicleTemplateDetailResponse response = FetchVehicleTemplateDetailResponse.builder()
                .requestId(baseRequest.getRequestId())
                .requestDateTime(baseRequest.getRequestDateTime())
                .channel(baseRequest.getChannel())
                .result(apiResultFactory.buildSuccess())
                .data(FetchVehicleTemplateDetailResponse.FetchVehicleTemplateDetailResponseData.builder()
                        .id(result.id())
                        .merchantId(result.merchantId())
                        .code(result.code())
                        .name(result.name())
                        .manufacturer(result.manufacturer())
                        .model(result.model())
                        .seatCapacity(result.seatCapacity())
                        .category(result.category())
                        .type(result.type())
                        .fuelType(result.fuelType())
                        .hasFloor(result.hasFloor())
                        .ticketPrice(result.ticketPrice())
                        .status(result.status())
                        .build())
                .build();

        return HttpUtils.buildResponse(baseRequest, response);
    }
}
