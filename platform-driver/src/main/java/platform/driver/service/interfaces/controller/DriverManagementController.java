package platform.driver.service.interfaces.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import vn.com.go.routex.identity.security.log.SystemLog;
import platform.driver.service.application.services.DriverProfileService;
import platform.driver.service.interfaces.mapper.DriverProfileApiMapper;
import platform.driver.service.interfaces.mapper.DriverProfileApiResponseMapper;
import platform.driver.service.interfaces.models.driver.request.CreateProfileRequest;
import platform.driver.service.interfaces.models.driver.request.DeleteProfileRequest;
import platform.driver.service.interfaces.models.driver.request.DriverProfileRequest;
import platform.driver.service.interfaces.models.driver.request.UpdateDriverStatusRequest;
import platform.driver.service.interfaces.models.driver.request.UpdateProfileRequest;
import platform.driver.service.interfaces.models.driver.response.CreateProfileResponse;
import platform.driver.service.interfaces.models.driver.response.DeleteProfileResponse;
import platform.driver.service.interfaces.models.driver.response.DriverProfileResponse;
import platform.driver.service.interfaces.models.driver.response.UpdateDriverStatusResponse;
import platform.driver.service.interfaces.models.driver.response.UpdateProfileResponse;

import static platform.core.common.service.persistence.constant.ApiConstant.API_PATH;
import static platform.core.common.service.persistence.constant.ApiConstant.API_VERSION;
import static platform.core.common.service.persistence.constant.ApiConstant.CREATE_PROFILE;
import static platform.core.common.service.persistence.constant.ApiConstant.DELETE_PROFILE;
import static platform.core.common.service.persistence.constant.ApiConstant.DRIVER_PREFIX;
import static platform.core.common.service.persistence.constant.ApiConstant.GET_DRIVER;
import static platform.core.common.service.persistence.constant.ApiConstant.UPDATE_PROFILE;
import static platform.core.common.service.persistence.constant.ApiConstant.UPDATE_STATUS;


@RestController
@RequestMapping(API_PATH + API_VERSION + DRIVER_PREFIX)
@RequiredArgsConstructor
public class DriverManagementController {

    private final DriverProfileService driverProfileService;
    private final SystemLog sLog = SystemLog.getLogger(this.getClass());

    @InitBinder
    public void initBinder(WebDataBinder webDataBinder, WebRequest webRequest) {
        webDataBinder.setDisallowedFields("requestId", "requestDateTime", "channel", "data");
    }


    @PostMapping(CREATE_PROFILE)
    public ResponseEntity<CreateProfileResponse> createDriverProfile(@Valid @RequestBody CreateProfileRequest request) {
        sLog.info("[UPDATE-PROFILE] Update Driver Profile Request: {}", request);
        var profile = driverProfileService.create(DriverProfileApiMapper.toCommand(request));
        return ResponseEntity.ok(DriverProfileApiResponseMapper.toCreateProfileResponse(request, profile));
    }

    @PostMapping(UPDATE_PROFILE)
    public ResponseEntity<UpdateProfileResponse> updateDriverProfile(@Valid @RequestBody UpdateProfileRequest request) {
        var profile = driverProfileService.update(DriverProfileApiMapper.toCommand(request));
        return ResponseEntity.ok(DriverProfileApiResponseMapper.toUpdateProfileResponse(request, profile));
    }

    @PostMapping(DELETE_PROFILE)
    public ResponseEntity<DeleteProfileResponse> deleteDriverProfile(@Valid @RequestBody DeleteProfileRequest request) {
        var profile = driverProfileService.delete(DriverProfileApiMapper.toCommand(request));
        return ResponseEntity.ok(DriverProfileApiResponseMapper.toDeleteProfileResponse(request, profile));
    }

    @PostMapping(GET_DRIVER)
    public ResponseEntity<DriverProfileResponse> getDriverProfile(@Valid @RequestBody DriverProfileRequest request) {
        var view = driverProfileService.get(DriverProfileApiMapper.toQuery(request));
        return ResponseEntity.ok(DriverProfileApiResponseMapper.toDriverProfileResponse(request, view));
    }

    @PostMapping(UPDATE_STATUS)
    public ResponseEntity<UpdateDriverStatusResponse> updateDriverStatus(@Valid @RequestBody UpdateDriverStatusRequest request) {
        var profile = driverProfileService.updateStatus(DriverProfileApiMapper.toCommand(request));
        return ResponseEntity.ok(DriverProfileApiResponseMapper.toUpdateDriverStatusResponse(request, profile));
    }
}
