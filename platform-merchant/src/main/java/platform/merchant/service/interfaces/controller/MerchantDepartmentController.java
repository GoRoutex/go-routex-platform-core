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
import platform.merchant.service.application.command.department.CreateDepartmentCommand;
import platform.merchant.service.application.command.department.CreateDepartmentResult;
import platform.merchant.service.application.command.department.DeleteDepartmentCommand;
import platform.merchant.service.application.command.department.DeleteDepartmentResult;
import platform.merchant.service.application.command.department.FetchDepartmentQuery;
import platform.merchant.service.application.command.department.FetchDepartmentResult;
import platform.merchant.service.application.command.department.GetDepartmentDetailQuery;
import platform.merchant.service.application.command.department.GetDepartmentDetailResult;
import platform.merchant.service.application.command.department.UpdateDepartmentCommand;
import platform.merchant.service.application.command.department.UpdateDepartmentResult;
import platform.merchant.service.application.service.DepartmentManagementService;
import platform.core.common.service.persistence.utils.ApiRequestUtils;
import platform.merchant.service.infrastructure.persistence.utils.HttpUtils;
import platform.merchant.service.interfaces.factory.ApiResultFactory;
import platform.core.common.service.api.BaseRequest;
import platform.merchant.service.interfaces.model.department.CreateDepartmentRequest;
import platform.merchant.service.interfaces.model.department.CreateDepartmentResponse;
import platform.merchant.service.interfaces.model.department.DeleteDepartmentRequest;
import platform.merchant.service.interfaces.model.department.DeleteDepartmentResponse;
import platform.merchant.service.interfaces.model.department.FetchDepartmentResponse;
import platform.merchant.service.interfaces.model.department.GetDepartmentDetailResponse;
import platform.merchant.service.interfaces.model.department.UpdateDepartmentRequest;
import platform.merchant.service.interfaces.model.department.UpdateDepartmentResponse;

import java.util.List;
import java.util.stream.Collectors;

import static platform.core.common.service.persistence.constant.ApiConstant.API_PATH;
import static platform.core.common.service.persistence.constant.ApiConstant.API_VERSION;
import static platform.core.common.service.persistence.constant.ApiConstant.CREATE_PATH;
import static platform.core.common.service.persistence.constant.ApiConstant.DELETE_PATH;
import static platform.core.common.service.persistence.constant.ApiConstant.DEPARTMENT;
import static platform.core.common.service.persistence.constant.ApiConstant.DETAIL_PATH;
import static platform.core.common.service.persistence.constant.ApiConstant.FETCH_PATH;
import static platform.core.common.service.persistence.constant.ApiConstant.MERCHANT_SERVICE;
import static platform.core.common.service.persistence.constant.ApiConstant.UPDATE_PATH;

@RestController
@RequestMapping(API_PATH + API_VERSION + MERCHANT_SERVICE)
@PreAuthorize("hasAuthority('points:management') or hasRole('MERCHANT_OWNER')")
@RequiredArgsConstructor
public class MerchantDepartmentController {

    private final DepartmentManagementService departmentManagementService;
    private final ApiResultFactory apiResultFactory;
    private final SystemLog sLog = SystemLog.getLogger(this.getClass());

    @InitBinder
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.setDisallowedFields("requestId", "requestDateTime", "channel", "data");
    }

    @PostMapping(DEPARTMENT + CREATE_PATH)
    public ResponseEntity<CreateDepartmentResponse> createDepartment(@Valid @RequestBody CreateDepartmentRequest request,
                                                                     HttpServletRequest servletRequest) {

        sLog.info("[OPERATION-POINT] Create Operation Point Request: {}", request);
        String merchantId = ApiRequestUtils.requireMerchantId(servletRequest, request);
        CreateDepartmentResult result = departmentManagementService.createDepartment(CreateDepartmentCommand.builder()
                .context(HttpUtils.toContext(request, merchantId))
                .merchantId(merchantId)
                .name(request.getData().getName())
                .type(request.getData().getType())
                .address(request.getData().getAddress())
                .wardId(request.getData().getWardId())
                .provinceId(request.getData().getProvinceId())
                .openingTime(request.getData().getOpeningTime())
                .onlineOpeningTime(request.getData().getOnlineOpeningTime())
                .onlineClosingTime(request.getData().getOnlineClosingTime())
                .closingTime(request.getData().getClosingTime())
                .latitude(request.getData().getLatitude())
                .longitude(request.getData().getLongitude())
                .status(request.getData().getStatus())
                .build());

        CreateDepartmentResponse response = CreateDepartmentResponse.builder()
                .requestId(request.getRequestId())
                .requestDateTime(request.getRequestDateTime())
                .channel(request.getChannel())
                .result(apiResultFactory.buildSuccess())
                .data(CreateDepartmentResponse.CreateDepartmentResponseData.builder()
                        .id(result.id())
                        .name(result.name())
                        .type(result.type())
                        .address(result.address())
                        .wardId(result.wardId())
                        .wardName(result.wardName())
                        .provinceId(result.provinceId())
                        .provinceName(result.provinceName())
                        .openingTime(result.openingTime())
                        .closingTime(result.closingTime())
                        .onlineOpeningTime(result.onlineOpeningTime())
                        .onlineClosingTime(result.onlineClosingTime())
                        .latitude(result.latitude())
                        .longitude(result.longitude())
                        .status(result.status())
                        .build())
                .build();


        sLog.info("[OPERATION-POINT] Create Operation Point Response: {}", response);
        return HttpUtils.buildResponse(request, response);
    }

    @PostMapping(DEPARTMENT + UPDATE_PATH)
    public ResponseEntity<UpdateDepartmentResponse> updateDepartment(@Valid @RequestBody UpdateDepartmentRequest request,
                                                                     HttpServletRequest servletRequest) {

        sLog.info("[OPERATION-POINT] Update Operation Point Request: {}", request);
        String merchantId = ApiRequestUtils.requireMerchantId(servletRequest, request);

        UpdateDepartmentResult result = departmentManagementService.updateDepartment(UpdateDepartmentCommand.builder()
                .context(HttpUtils.toContext(request, merchantId))
                .merchantId(merchantId)
                .id(request.getData().getId())
                .name(request.getData().getName())
                .type(request.getData().getType())
                .address(request.getData().getAddress())
                .wardId(request.getData().getWardId())
                .provinceId(request.getData().getProvinceId())
                .openingTime(request.getData().getOpeningTime())
                .closingTime(request.getData().getClosingTime())
                .onlineOpeningTime(request.getData().getOnlineOpeningTime())
                .onlineClosingTime(request.getData().getOnlineClosingTime())
                .latitude(request.getData().getLatitude())
                .longitude(request.getData().getLongitude())
                .status(request.getData().getStatus())
                .build());

        UpdateDepartmentResponse response = UpdateDepartmentResponse.builder()
                .requestId(request.getRequestId())
                .requestDateTime(request.getRequestDateTime())
                .channel(request.getChannel())
                .result(apiResultFactory.buildSuccess())
                .data(UpdateDepartmentResponse.UpdateDepartmentResponseData.builder()
                        .id(result.id())
                        .name(result.name())
                        .type(result.type())
                        .address(result.address())
                        .wardId(result.wardId())
                        .wardName(result.wardName())
                        .provinceId(result.provinceId())
                        .provinceName(result.provinceName())
                        .openingTime(result.openingTime())
                        .closingTime(result.closingTime())
                        .onlineOpeningTime(result.onlineOpeningTime())
                        .onlineClosingTime(result.onlineClosingTime())
                        .latitude(result.latitude())
                        .longitude(result.longitude())
                        .status(result.status())
                        .build())
                .build();

        sLog.info("[OPERATION-POINT] Update Operation Point Response: {}", response);
        return HttpUtils.buildResponse(request, response);
    }

    @PostMapping(DEPARTMENT + DELETE_PATH)
    public ResponseEntity<DeleteDepartmentResponse> deleteDepartment(@Valid @RequestBody DeleteDepartmentRequest request,
                                                                     HttpServletRequest servletRequest) {

        sLog.info("[OPERATION-POINT] Delete Operation Point Request: {}", request);
        String merchantId = ApiRequestUtils.requireMerchantId(servletRequest, request);

        DeleteDepartmentResult result = departmentManagementService.deleteDepartment(DeleteDepartmentCommand.builder()
                .context(HttpUtils.toContext(request, merchantId))
                .merchantId(merchantId)
                .id(request.getData().getId())
                .build());

        DeleteDepartmentResponse response = DeleteDepartmentResponse.builder()
                .requestId(request.getRequestId())
                .requestDateTime(request.getRequestDateTime())
                .channel(request.getChannel())
                .result(apiResultFactory.buildSuccess())
                .data(DeleteDepartmentResponse.DeleteDepartmentResponseData.builder()
                        .id(result.id())
                        .status(result.status())
                        .build())
                .build();

        sLog.info("[OPERATION-POINT] Delete Operation Point Response: {}", response);
        return HttpUtils.buildResponse(request, response);
    }

    @GetMapping(DEPARTMENT + FETCH_PATH)
    public ResponseEntity<FetchDepartmentResponse> fetchDepartment(@RequestParam int pageNumber,
                                                                   @RequestParam int pageSize,
                                                                   @RequestParam(required = false) String provinceId,
                                                                   HttpServletRequest servletRequest) {

        BaseRequest baseRequest = ApiRequestUtils.getBaseRequestOrDefault(servletRequest);
        String merchantId = ApiRequestUtils.requireMerchantId(servletRequest, baseRequest);

        FetchDepartmentResult result = departmentManagementService.fetchDepartment(FetchDepartmentQuery.builder()
                .context(HttpUtils.toContext(baseRequest, merchantId))
                .provinceId(provinceId)
                .pageNumber(String.valueOf(pageNumber))
                .pageSize(String.valueOf(pageSize))
                .merchantId(merchantId)
                .build());

        List<FetchDepartmentResponse.FetchDepartmentResponseData> items = result.items().stream()
                .map(p -> FetchDepartmentResponse.FetchDepartmentResponseData.builder()
                        .id(p.id())
                        .name(p.name())
                        .type(p.type())
                        .address(p.address())
                        .wardId(p.wardId())
                        .wardName(p.wardName())
                        .provinceId(p.provinceId())
                        .provinceName(p.provinceName())
                        .openingTime(p.openingTime())
                        .closingTime(p.closingTime())
                        .onlineOpeningTime(p.onlineOpeningTime())
                        .onlineClosingTime(p.onlineClosingTime())
                        .latitude(p.latitude())
                        .longitude(p.longitude())
                        .status(p.status())
                        .build())
                .collect(Collectors.toList());

        FetchDepartmentResponse response = FetchDepartmentResponse.builder()
                .requestId(baseRequest.getRequestId())
                .requestDateTime(baseRequest.getRequestDateTime())
                .channel(baseRequest.getChannel())
                .result(apiResultFactory.buildSuccess())
                .data(FetchDepartmentResponse.FetchDepartmentResponsePage.builder()
                        .items(items)
                        .pagination(FetchDepartmentResponse.Pagination.builder()
                                .pageNumber(result.pageNumber())
                                .pageSize(result.pageSize())
                                .totalElements(result.totalElements())
                                .totalPages(result.totalPages())
                                .build())
                        .build())
                .build();

        return HttpUtils.buildResponse(baseRequest, response);
    }

    @GetMapping(DEPARTMENT + DETAIL_PATH)
    public ResponseEntity<GetDepartmentDetailResponse> getDepartmentDetail(@RequestParam String departmentId,
                                                                           HttpServletRequest servletRequest) {
        BaseRequest baseRequest = ApiRequestUtils.getBaseRequestOrDefault(servletRequest);
        String merchantId = ApiRequestUtils.requireMerchantId(servletRequest, baseRequest);

        GetDepartmentDetailResult result = departmentManagementService.getDepartmentDetail(
                GetDepartmentDetailQuery.builder()
                        .context(HttpUtils.toContext(baseRequest, merchantId))
                        .merchantId(merchantId)
                        .departmentId(departmentId)
                        .build()
        );

        GetDepartmentDetailResponse response = GetDepartmentDetailResponse.builder()
                .requestId(baseRequest.getRequestId())
                .requestDateTime(baseRequest.getRequestDateTime())
                .channel(baseRequest.getChannel())
                .result(apiResultFactory.buildSuccess())
                .data(GetDepartmentDetailResponse.GetDepartmentDetailResponseData.builder()
                        .id(result.id())
                        .name(result.name())
                        .type(result.type())
                        .address(result.address())
                        .wardId(result.wardId())
                        .wardName(result.wardName())
                        .provinceId(result.provinceId())
                        .provinceName(result.provinceName())
                        .openingTime(result.openingTime())
                        .closingTime(result.closingTime())
                        .onlineOpeningTime(result.onlineOpeningTime())
                        .onlineClosingTime(result.onlineClosingTime())
                        .latitude(result.latitude())
                        .longitude(result.longitude())
                        .status(result.status())
                        .build())
                .build();

        return HttpUtils.buildResponse(baseRequest, response);
    }
}
