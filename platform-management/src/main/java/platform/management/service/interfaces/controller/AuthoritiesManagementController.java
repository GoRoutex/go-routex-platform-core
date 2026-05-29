package platform.management.service.interfaces.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.com.go.routex.identity.security.log.SystemLog;
import platform.management.service.application.command.authorities.AddPermissionCommand;
import platform.management.service.application.command.authorities.AddPermissionResult;
import platform.management.service.application.command.authorities.AddRoleCommand;
import platform.management.service.application.command.authorities.AddRoleResult;
import platform.management.service.application.command.authorities.SetPermissionCommand;
import platform.management.service.application.command.authorities.SetPermissionResult;
import platform.management.service.application.command.authorities.SetRoleCommand;
import platform.management.service.application.command.authorities.SetRoleResult;
import platform.management.service.application.services.AuthoritiesManagementService;
import platform.management.service.infrastructure.persistence.utils.HttpUtils;
import platform.management.service.interfaces.factory.ApiResultFactory;
import platform.management.service.interfaces.models.authorities.AddPermissionRequest;
import platform.management.service.interfaces.models.authorities.AddPermissionResponse;
import platform.management.service.interfaces.models.authorities.AddRoleRequest;
import platform.management.service.interfaces.models.authorities.AddRoleResponse;
import platform.management.service.interfaces.models.authorities.SetPermissionRequest;
import platform.management.service.interfaces.models.authorities.SetPermissionResponse;
import platform.management.service.interfaces.models.authorities.SetRoleRequest;
import platform.management.service.interfaces.models.authorities.SetRoleResponse;

import static vn.com.routex.platform.common.constant.ApiConstant.ADD_PERMISSIONS;
import static vn.com.routex.platform.common.constant.ApiConstant.ADD_ROLES;
import static vn.com.routex.platform.common.constant.ApiConstant.API_PATH;
import static vn.com.routex.platform.common.constant.ApiConstant.API_VERSION;
import static vn.com.routex.platform.common.constant.ApiConstant.AUTHORITIES_PATH;
import static vn.com.routex.platform.common.constant.ApiConstant.MANAGEMENT_PATH;
import static vn.com.routex.platform.common.constant.ApiConstant.SET_PERMISSIONS;
import static vn.com.routex.platform.common.constant.ApiConstant.SET_ROLE;

@RestController
@RequestMapping(API_PATH + API_VERSION + MANAGEMENT_PATH)
@RequiredArgsConstructor
//@PreAuthorize("hasRole('ADMIN') or hasAuthority('authorities:management')")
public class AuthoritiesManagementController {


    private final AuthoritiesManagementService authoritiesManagementService;
    private final ApiResultFactory apiResultFactory;
    private final SystemLog sLog = SystemLog.getLogger(this.getClass());

    @InitBinder
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.setDisallowedFields("requestId", "requestDateTime", "channel", "data");
    }

    @PostMapping(AUTHORITIES_PATH + ADD_ROLES)
    public ResponseEntity<AddRoleResponse> addRole(@Valid @RequestBody AddRoleRequest request) {
        sLog.info("[ROLE-MANAGEMENT] Add Role Request: {}", request);
        AddRoleResult result = authoritiesManagementService.addRole(AddRoleCommand.builder()
                .code(request.getData().getCode())
                .name(request.getData().getName())
                .description(request.getData().getDescription())
                .creator(request.getData().getCreator())
                .enabled(request.getData().isEnabled())
                .requestId(request.getRequestId())
                .requestDateTime(request.getRequestDateTime())
                .channel(request.getChannel())
                .build());

        AddRoleResponse response = AddRoleResponse.builder()
                .result(apiResultFactory.buildSuccess())
                .data(AddRoleResponse.AddRoleResponseData.builder()
                        .code(result.code())
                        .name(result.name())
                        .creator(result.creator())
                        .description(result.description())
                        .build())
                .build();

        sLog.info("[ROLE-MANAGEMENT] Add Role Response: {}", response);
        return HttpUtils.buildResponse(request, response);
    }
    @PostMapping(AUTHORITIES_PATH + ADD_PERMISSIONS)
    public ResponseEntity<AddPermissionResponse> addPermission(@Valid @RequestBody AddPermissionRequest request) {
        sLog.info("[PERMISSION-MANAGEMENT] Add Permission Request: {}", request);
        AddPermissionResult result = authoritiesManagementService.addPermission(AddPermissionCommand.builder()
                .code(request.getData().getCode())
                .name(request.getData().getName())
                .description(request.getData().getDescription())
                .creator(request.getData().getCreator())
                .enabled(request.getData().isEnabled())
                .requestId(request.getRequestId())
                .requestDateTime(request.getRequestDateTime())
                .channel(request.getChannel())
                .build());

        AddPermissionResponse response = AddPermissionResponse.builder()
                .result(apiResultFactory.buildSuccess())
                .data(AddPermissionResponse.AddPermissionResponseData.builder()
                        .code(result.code())
                        .name(result.name())
                        .creator(result.creator())
                        .description(result.description())
                        .build())
                .build();


        sLog.info("[PERMISSION-MANAGEMENT] Add Permission Response: {}", response);
        return HttpUtils.buildResponse(request, response);
    }

    @PostMapping(AUTHORITIES_PATH + SET_ROLE)
    public ResponseEntity<SetRoleResponse> setRole(@Valid @RequestBody SetRoleRequest request) {
        sLog.info("[ROLE-MANAGEMENT] Set Role Request: {}", request);
        SetRoleResult result = authoritiesManagementService.setRole(SetRoleCommand.builder()
                .userId(request.getData().getUserId())
                .roleId(request.getData().getRoleId())
                .requestId(request.getRequestId())
                .requestDateTime(request.getRequestDateTime())
                .channel(request.getChannel())
                .build());

        SetRoleResponse response = SetRoleResponse.builder()
                .result(apiResultFactory.buildSuccess())
                .data(SetRoleResponse.SetRoleResponseData.builder()
                        .userId(result.userId())
                        .roleId(result.roleId())
                        .assignedAt(result.assignedAt())
                        .build())
                .build();

        sLog.info("[ROLE-MANAGEMENT] Set Role Response: {}", response);
        return HttpUtils.buildResponse(request, response);
    }

    @PostMapping(AUTHORITIES_PATH + SET_PERMISSIONS)
    public ResponseEntity<SetPermissionResponse> setPermission(@Valid @RequestBody SetPermissionRequest request) {
        sLog.info("[PERMISSION-MANAGEMENT] Set Permission Request: {}", request);
        SetPermissionResult result = authoritiesManagementService.setPermission(SetPermissionCommand.builder()
                .roleId(request.getData().getRoleId())
                .authoritiesCode(request.getData().getAuthoritiesCode())
                .requestId(request.getRequestId())
                .requestDateTime(request.getRequestDateTime())
                .channel(request.getChannel())
                .build());

        SetPermissionResponse response = SetPermissionResponse.builder()
                .result(apiResultFactory.buildSuccess())
                .data(SetPermissionResponse.SetPermissionResponseData.builder()
                        .roleId(result.roleId())
                        .authorities(result.authorities())
                        .build())
                .build();
        sLog.info("[PERMISSION-MANAGEMENT] Set Permission Response: {}", response);
        return HttpUtils.buildResponse(request, response);
    }

}
