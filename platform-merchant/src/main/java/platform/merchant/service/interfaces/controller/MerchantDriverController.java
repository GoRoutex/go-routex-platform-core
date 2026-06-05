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
import platform.core.common.service.persistence.utils.ApiRequestUtils;
import platform.merchant.service.application.command.driver.CreateDriverCommand;
import platform.merchant.service.application.command.driver.CreateDriverResult;
import platform.merchant.service.application.command.driver.DeleteDriverCommand;
import platform.merchant.service.application.command.driver.DeleteDriverResult;
import platform.merchant.service.application.command.driver.FetchDriverDetailQuery;
import platform.merchant.service.application.command.driver.FetchDriverDetailResult;
import platform.merchant.service.application.command.driver.FetchDriversQuery;
import platform.merchant.service.application.command.driver.FetchDriversResult;
import platform.merchant.service.application.command.driver.UpdateDriverCommand;
import platform.merchant.service.application.command.driver.UpdateDriverResult;
import platform.merchant.service.application.service.DriverManagementService;
import platform.merchant.service.infrastructure.persistence.utils.HttpUtils;
import platform.merchant.service.interfaces.factory.ApiResultFactory;
import platform.merchant.service.interfaces.model.driver.CreateDriverRequest;
import platform.merchant.service.interfaces.model.driver.CreateDriverResponse;
import platform.merchant.service.interfaces.model.driver.DeleteDriverRequest;
import platform.merchant.service.interfaces.model.driver.DeleteDriverResponse;
import platform.merchant.service.interfaces.model.driver.FetchDriverDetailResponse;
import platform.merchant.service.interfaces.model.driver.FetchDriverResponse;
import platform.merchant.service.interfaces.model.driver.FetchDriverResponse.FetchDriverMerchantInfo;
import platform.merchant.service.interfaces.model.driver.FetchDriverResponse.FetchDriverResponseData;
import platform.merchant.service.interfaces.model.driver.FetchDriverResponse.FetchDriverUserInfo;
import platform.merchant.service.interfaces.model.driver.UpdateDriverRequest;
import platform.merchant.service.interfaces.model.driver.UpdateDriverResponse;
import vn.com.go.routex.identity.security.log.SystemLog;

import java.util.List;
import java.util.stream.Collectors;

import static platform.core.common.service.persistence.constant.ApiConstant.API_PATH;
import static platform.core.common.service.persistence.constant.ApiConstant.API_VERSION;
import static platform.core.common.service.persistence.constant.ApiConstant.CREATE_PATH;
import static platform.core.common.service.persistence.constant.ApiConstant.DELETE_PATH;
import static platform.core.common.service.persistence.constant.ApiConstant.DETAIL_PATH;
import static platform.core.common.service.persistence.constant.ApiConstant.DRIVER_PATH;
import static platform.core.common.service.persistence.constant.ApiConstant.FETCH_PATH;
import static platform.core.common.service.persistence.constant.ApiConstant.MERCHANT_SERVICE;
import static platform.core.common.service.persistence.constant.ApiConstant.UPDATE_PATH;

@RestController
@RequestMapping(API_PATH + API_VERSION + MERCHANT_SERVICE)
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('driver:management') or hasRole('MERCHANT_OWNER')")
public class MerchantDriverController {

    private final DriverManagementService driverManagementService;
    private final ApiResultFactory apiResultFactory;
    private final SystemLog sLog = SystemLog.getLogger(this.getClass());

    @InitBinder
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.setDisallowedFields("requestId", "requestDateTime", "channel", "data");
    }

    @PostMapping(DRIVER_PATH + CREATE_PATH)
    public ResponseEntity<CreateDriverResponse> createDriver(@Valid @RequestBody CreateDriverRequest request,
                                                             HttpServletRequest servletRequest) {
        sLog.info("[DRIVER-MANAGEMENT] Create Driver Request: {}", request);
        String merchantId = ApiRequestUtils.requireMerchantId(servletRequest, request);

        CreateDriverResult result = driverManagementService.createDriver(CreateDriverCommand.builder()
                .context(HttpUtils.toContext(request, merchantId))
                .merchantId(merchantId)
                .fullName(request.getData().getFullName())
                .creator(request.getData().getCreator())
                .userId(request.getData().getUserId())
                .employeeCode(request.getData().getEmployeeCode())
                .emergencyContactName(request.getData().getEmergencyContactName())
                .emergencyContactPhone(request.getData().getEmergencyContactPhone())
                .status(request.getData().getStatus())
                .operationStatus(request.getData().getOperationStatus())
                .rating(request.getData().getRating())
                .totalTrips(request.getData().getTotalTrips())
                .licenseClass(request.getData().getLicenseClass())
                .licenseNumber(request.getData().getLicenseNumber())
                .licenseIssueDate(request.getData().getLicenseIssueDate())
                .licenseExpiryDate(request.getData().getLicenseExpiryDate())
                .pointsDelta(request.getData().getPointsDelta())
                .pointsReason(request.getData().getPointsReason())
                .kycVerified(request.getData().getKycVerified())
                .trainingCompleted(request.getData().getTrainingCompleted())
                .note(request.getData().getNote())
                .build());

        CreateDriverResponse response = CreateDriverResponse.builder()
                .result(apiResultFactory.buildSuccess())
                .data(CreateDriverResponse.CreateDriverResponseData.builder()
                        .id(result.id())
                        .merchantId(result.merchantId())
                        .creator(result.creator())
                        .userId(result.userId())
                        .employeeCode(result.employeeCode())
                        .emergencyContactName(result.emergencyContactName())
                        .emergencyContactPhone(result.emergencyContactPhone())
                        .status(result.status())
                        .operationStatus(result.operationStatus())
                        .rating(result.rating())
                        .totalTrips(result.totalTrips())
                        .licenseClass(result.licenseClass())
                        .licenseNumber(result.licenseNumber())
                        .licenseIssueDate(result.licenseIssueDate())
                        .licenseExpiryDate(result.licenseExpiryDate())
                        .pointsDelta(result.pointsDelta())
                        .pointsReason(result.pointsReason())
                        .kycVerified(result.kycVerified())
                        .trainingCompleted(result.trainingCompleted())
                        .note(result.note())
                        .build())
                .build();

        sLog.info("[DRIVER-MANAGEMENT] Create Driver Response: {}", response);
        return HttpUtils.buildResponse(request, response);
    }

    @PostMapping(DRIVER_PATH + UPDATE_PATH)
    public ResponseEntity<UpdateDriverResponse> updateDriver(@Valid @RequestBody UpdateDriverRequest request,
                                                             HttpServletRequest servletRequest) {
        sLog.info("[DRIVER-MANAGEMENT] Update Driver Request: {}", request);
        String merchantId = ApiRequestUtils.requireMerchantId(servletRequest, request);

        UpdateDriverResult result = driverManagementService.updateDriver(UpdateDriverCommand.builder()
                .context(HttpUtils.toContext(request, merchantId))
                .merchantId(merchantId)
                .creator(request.getData().getCreator())
                .driverId(request.getData().getDriverId())
                .userId(request.getData().getUserId())
                .employeeCode(request.getData().getEmployeeCode())
                .emergencyContactName(request.getData().getEmergencyContactName())
                .emergencyContactPhone(request.getData().getEmergencyContactPhone())
                .status(request.getData().getStatus())
                .operationStatus(request.getData().getOperationStatus())
                .rating(request.getData().getRating())
                .totalTrips(request.getData().getTotalTrips())
                .licenseClass(request.getData().getLicenseClass())
                .licenseNumber(request.getData().getLicenseNumber())
                .licenseIssueDate(request.getData().getLicenseIssueDate())
                .licenseExpiryDate(request.getData().getLicenseExpiryDate())
                .pointsDelta(request.getData().getPointsDelta())
                .pointsReason(request.getData().getPointsReason())
                .kycVerified(request.getData().getKycVerified())
                .trainingCompleted(request.getData().getTrainingCompleted())
                .note(request.getData().getNote())
                .build());

        UpdateDriverResponse response = UpdateDriverResponse.builder()
                .result(apiResultFactory.buildSuccess())
                .data(UpdateDriverResponse.UpdateDriverResponseData.builder()
                        .id(result.id())
                        .merchantId(result.merchantId())
                        .creator(result.creator())
                        .userId(result.userId())
                        .employeeCode(result.employeeCode())
                        .emergencyContactName(result.emergencyContactName())
                        .emergencyContactPhone(result.emergencyContactPhone())
                        .status(result.status())
                        .operationStatus(result.operationStatus())
                        .rating(result.rating())
                        .totalTrips(result.totalTrips())
                        .licenseClass(result.licenseClass())
                        .licenseNumber(result.licenseNumber())
                        .licenseIssueDate(result.licenseIssueDate())
                        .licenseExpiryDate(result.licenseExpiryDate())
                        .pointsDelta(result.pointsDelta())
                        .pointsReason(result.pointsReason())
                        .kycVerified(result.kycVerified())
                        .trainingCompleted(result.trainingCompleted())
                        .note(result.note())
                        .build())
                .build();

        sLog.info("[DRIVER-MANAGEMENT] Update Driver Response: {}", response);
        return HttpUtils.buildResponse(request, response);
    }

    @PostMapping(DRIVER_PATH + DELETE_PATH)
    public ResponseEntity<DeleteDriverResponse> deleteDriver(@Valid @RequestBody DeleteDriverRequest request,
                                                             HttpServletRequest servletRequest) {
        sLog.info("[DRIVER-MANAGEMENT] Delete Driver Request: {}", request);
        String merchantId = ApiRequestUtils.requireMerchantId(servletRequest, request);

        DeleteDriverResult result = driverManagementService.deleteDriver(DeleteDriverCommand.builder()
                .context(HttpUtils.toContext(request, merchantId))
                .merchantId(merchantId)
                .creator(request.getData().getCreator())
                .driverId(request.getData().getDriverId())
                .build());

        DeleteDriverResponse response = DeleteDriverResponse.builder()
                .result(apiResultFactory.buildSuccess())
                .data(DeleteDriverResponse.DeleteDriverResponseData.builder()
                        .id(result.id())
                        .status(result.status())
                        .operationStatus(result.operationStatus())
                        .build())
                .build();

        sLog.info("[DRIVER-MANAGEMENT] Delete Driver Response: {}", response);
        return HttpUtils.buildResponse(request, response);
    }

    @GetMapping(DRIVER_PATH + FETCH_PATH)
    public ResponseEntity<FetchDriverResponse> fetchDrivers(HttpServletRequest servletRequest,
                                                            @RequestParam(required = false) String status,
                                                            @RequestParam(defaultValue = "1") int pageNumber,
                                                            @RequestParam(defaultValue = "10") int pageSize) {
        BaseRequest baseRequest = ApiRequestUtils.getBaseRequestOrDefault(servletRequest);
        String merchantId = ApiRequestUtils.requireMerchantId(servletRequest, baseRequest);

        FetchDriversResult result = driverManagementService.fetchDrivers(FetchDriversQuery.builder()
                .context(HttpUtils.toContext(baseRequest, merchantId))
                .merchantId(merchantId)
                .status(status)
                .pageNumber(String.valueOf(pageNumber))
                .pageSize(String.valueOf(pageSize))
                .build());


        List<FetchDriverResponseData> items = result.items().stream()
                .map(driver -> {

                    FetchDriverMerchantInfo merchantInfo = FetchDriverMerchantInfo.builder()
                            .merchantId(driver.merchantInfo().merchantId())
                            .merchantName(driver.merchantInfo().merchantName())
                            .build();

                    FetchDriverUserInfo userInfo = FetchDriverUserInfo.builder()
                            .userId(driver.userInfo().userId())
                            .phone(driver.userInfo().phone())
                            .email(driver.userInfo().email())
                            .fullName(driver.userInfo().fullName())
                            .build();

                    return FetchDriverResponseData.builder()
                            .id(driver.id())
                            .merchantInfo(merchantInfo)
                            .userInfo(userInfo)
                            .employeeCode(driver.employeeCode())
                            .emergencyContactName(driver.emergencyContactName())
                            .emergencyContactPhone(driver.emergencyContactPhone())
                            .status(driver.status())
                            .operationStatus(driver.operationStatus())
                            .rating(driver.rating())
                            .totalTrips(driver.totalTrips())
                            .licenseClass(driver.licenseClass())
                            .licenseNumber(driver.licenseNumber())
                            .licenseIssueDate(driver.licenseIssueDate())
                            .licenseExpiryDate(driver.licenseExpiryDate())
                            .pointsDelta(driver.pointsDelta())
                            .pointsReason(driver.pointsReason())
                            .kycVerified(driver.kycVerified())
                            .trainingCompleted(driver.trainingCompleted())
                            .note(driver.note())
                            .build();


                })
                .collect(Collectors.toList());

        FetchDriverResponse response = FetchDriverResponse.builder()
                .requestId(baseRequest.getRequestId())
                .requestDateTime(baseRequest.getRequestDateTime())
                .channel(baseRequest.getChannel())
                .result(apiResultFactory.buildSuccess())
                .data(FetchDriverResponse.FetchDriverResponsePage.builder()
                        .items(items)
                        .pagination(FetchDriverResponse.Pagination.builder()
                                .pageNumber(result.pageNumber())
                                .pageSize(result.pageSize())
                                .totalElements(result.totalElements())
                                .totalPages(result.totalPages())
                                .build())
                        .build())
                .build();

        sLog.info("[DRIVER-MANAGEMENT] Fetch Driver Response: {}", response);

        return HttpUtils.buildResponse(baseRequest, response);
    }

    @GetMapping(DRIVER_PATH + DETAIL_PATH)
    public ResponseEntity<FetchDriverDetailResponse> fetchDriverDetail(HttpServletRequest servletRequest,
                                                                       @RequestParam(required = false) String driverId,
                                                                       @RequestParam(required = false) String userId,
                                                                       @RequestParam(required = false) String employeeCode) {
        BaseRequest baseRequest = ApiRequestUtils.getBaseRequestOrDefault(servletRequest);
        String merchantId = ApiRequestUtils.requireMerchantId(servletRequest, baseRequest);

        FetchDriverDetailResult result = driverManagementService.fetchDriverDetail(FetchDriverDetailQuery.builder()
                .context(HttpUtils.toContext(baseRequest, merchantId))
                .merchantId(merchantId)
                .driverId(driverId)
                .userId(userId)
                .employeeCode(employeeCode)
                .build());

        FetchDriverDetailResponse.FetchDriverDetailMerchantInfo merchantInfo = FetchDriverDetailResponse.FetchDriverDetailMerchantInfo.builder()
                .merchantId(result.merchantInfo().merchantId())
                .merchantName(result.merchantInfo().merchantName())
                .build();

        FetchDriverDetailResponse.FetchDriverDetailUserInfo userInfo = FetchDriverDetailResponse.FetchDriverDetailUserInfo.builder()
                .userId(result.userInfo().userId())
                .fullName(result.userInfo().fullName())
                .phone(result.userInfo().phone())
                .email(result.userInfo().email())
                .build();

        FetchDriverDetailResponse response = FetchDriverDetailResponse.builder()
                .requestId(baseRequest.getRequestId())
                .requestDateTime(baseRequest.getRequestDateTime())
                .channel(baseRequest.getChannel())
                .result(apiResultFactory.buildSuccess())
                .data(FetchDriverDetailResponse.FetchDriverDetailResponseData.builder()
                        .id(result.id())
                        .merchantInfo(merchantInfo)
                        .userInfo(userInfo)
                        .employeeCode(result.employeeCode())
                        .emergencyContactName(result.emergencyContactName())
                        .emergencyContactPhone(result.emergencyContactPhone())
                        .status(result.status())
                        .operationStatus(result.operationStatus())
                        .rating(result.rating())
                        .totalTrips(result.totalTrips())
                        .licenseClass(result.licenseClass())
                        .licenseNumber(result.licenseNumber())
                        .licenseIssueDate(result.licenseIssueDate())
                        .licenseExpiryDate(result.licenseExpiryDate())
                        .pointsDelta(result.pointsDelta())
                        .pointsReason(result.pointsReason())
                        .kycVerified(result.kycVerified())
                        .trainingCompleted(result.trainingCompleted())
                        .note(result.note())
                        .build())
                .build();

        return HttpUtils.buildResponse(baseRequest, response);
    }
}
