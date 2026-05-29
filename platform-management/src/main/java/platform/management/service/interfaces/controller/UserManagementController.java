package platform.management.service.interfaces.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import platform.management.service.application.command.user.DeleteUserCommand;
import platform.management.service.application.command.user.DeleteUserResult;
import platform.management.service.application.command.user.FetchUserDetailQuery;
import platform.management.service.application.command.user.FetchUserDetailResult;
import platform.management.service.application.command.user.FetchUsersQuery;
import platform.management.service.application.command.user.FetchUsersResult;
import platform.management.service.application.command.user.GetUserProfileCommand;
import platform.management.service.application.command.user.GetUserProfileResult;
import platform.management.service.application.command.user.UpdateUserCommand;
import platform.management.service.application.command.user.UpdateUserResult;
import platform.management.service.application.services.UserManagementService;
import platform.management.service.application.services.UserProfileService;
import platform.management.service.infrastructure.persistence.utils.ApiRequestUtils;
import platform.management.service.infrastructure.persistence.utils.HttpUtils;
import platform.management.service.interfaces.factory.ApiResultFactory;
import platform.management.service.interfaces.models.user.DeleteUserRequest;
import platform.management.service.interfaces.models.user.DeleteUserResponse;
import platform.management.service.interfaces.models.user.FetchUserDetailResponse;
import platform.management.service.interfaces.models.user.FetchUsersResponse;
import platform.management.service.interfaces.models.user.GetUserProfileRequest;
import platform.management.service.interfaces.models.user.GetUserProfileResponse;
import platform.management.service.interfaces.models.user.UpdateUserRequest;
import platform.management.service.interfaces.models.user.UpdateUserResponse;
import platform.core.common.service.api.BaseRequest;

import java.util.stream.Collectors;

import static vn.com.routex.platform.common.constant.ApiConstant.API_PATH;
import static vn.com.routex.platform.common.constant.ApiConstant.API_VERSION;
import static vn.com.routex.platform.common.constant.ApiConstant.DELETE_PATH;
import static vn.com.routex.platform.common.constant.ApiConstant.DETAIL_PATH;
import static vn.com.routex.platform.common.constant.ApiConstant.FETCH_PATH;
import static vn.com.routex.platform.common.constant.ApiConstant.MANAGEMENT_PATH;
import static vn.com.routex.platform.common.constant.ApiConstant.PROFILE_PATH;
import static vn.com.routex.platform.common.constant.ApiConstant.UPDATE_PATH;
import static vn.com.routex.platform.common.constant.ApiConstant.USER_SERVICE;

@RestController
@RequestMapping( API_PATH + API_VERSION + MANAGEMENT_PATH)
@RequiredArgsConstructor
public class UserManagementController {
    private final ApiResultFactory apiResultFactory;
    private final UserProfileService userProfileService;
    private final UserManagementService userManagementService;

    @GetMapping(USER_SERVICE + FETCH_PATH)
    public ResponseEntity<FetchUsersResponse> fetchUsers(HttpServletRequest servletRequest,
                                                         @RequestParam(defaultValue = "1") int pageNumber,
                                                         @RequestParam(defaultValue = "10") int pageSize) {
        BaseRequest baseRequest = ApiRequestUtils.getBaseRequestOrDefault(servletRequest);

        FetchUsersResult result = userManagementService.fetchUsers(FetchUsersQuery.builder()
                .context(HttpUtils.toContext(baseRequest))
                .pageNumber(String.valueOf(pageNumber))
                .pageSize(String.valueOf(pageSize))
                .build());

        FetchUsersResponse response = FetchUsersResponse.builder()
                .result(apiResultFactory.buildSuccess())
                .data(FetchUsersResponse.FetchUsersResponsePage.builder()
                        .items(result.items().stream()
                                .map(item -> FetchUsersResponse.FetchUserResponseData.builder()
                                        .id(item.id())
                                        .email(item.email())
                                        .fullName(item.fullName())
                                        .phoneNumber(item.phoneNumber())
                                        .avatarUrl(item.avatarUrl())
                                        .dob(item.dob())
                                        .gender(item.gender())
                                        .phoneVerified(item.phoneVerified())
                                        .profileCompleted(item.profileCompleted())
                                        .emailVerified(item.emailVerified())
                                        .status(item.status())
                                        .language(item.language())
                                        .timezone(item.timezone())
                                        .createdAt(item.createdAt())
                                        .updatedAt(item.updatedAt())
                                        .build())
                                .collect(Collectors.toList()))
                        .pagination(FetchUsersResponse.Pagination.builder()
                                .pageNumber(result.pageNumber())
                                .pageSize(result.pageSize())
                                .totalElements(result.totalElements())
                                .totalPages(result.totalPages())
                                .build())
                        .build())
                .build();

        return HttpUtils.buildResponse(baseRequest, response);
    }

    @GetMapping(USER_SERVICE + DETAIL_PATH)
    public ResponseEntity<FetchUserDetailResponse> fetchUserDetail(HttpServletRequest servletRequest,
                                                                   @RequestParam String userId) {
        BaseRequest baseRequest = ApiRequestUtils.getBaseRequestOrDefault(servletRequest);

        FetchUserDetailResult result = userManagementService.fetchUserDetail(FetchUserDetailQuery.builder()
                .context(HttpUtils.toContext(baseRequest))
                .userId(userId)
                .build());

        FetchUserDetailResponse response = FetchUserDetailResponse.builder()
                .result(apiResultFactory.buildSuccess())
                .data(FetchUserDetailResponse.FetchUserDetailResponseData.builder()
                        .id(result.id())
                        .email(result.email())
                        .phoneNumber(result.phoneNumber())
                        .avatarUrl(result.avatarUrl())
                        .address(result.address())
                        .dob(result.dob())
                        .gender(result.gender())
                        .nationalId(result.nationalId())
                        .phoneVerified(result.phoneVerified())
                        .profileCompleted(result.profileCompleted())
                        .emailVerified(result.emailVerified())
                        .status(result.status())
                        .language(result.language())
                        .timezone(result.timezone())
                        .failLoginCount(result.failLoginCount())
                        .lastLoginAt(result.lastLoginAt())
                        .lockedUntil(result.lockedUntil())
                        .createdAt(result.createdAt())
                        .createdBy(result.createdBy())
                        .updatedAt(result.updatedAt())
                        .updatedBy(result.updatedBy())
                        .build())
                .build();

        return HttpUtils.buildResponse(baseRequest, response);
    }

    @PostMapping(USER_SERVICE + PROFILE_PATH)
    public ResponseEntity<GetUserProfileResponse> getProfiles(@Valid @RequestBody GetUserProfileRequest request) {
        GetUserProfileResult result = userProfileService.getUserProfile(GetUserProfileCommand.builder()
                .context(HttpUtils.toContext(request))
                .userId(request.getData().getUserId())
                .build());


        return ResponseEntity.ok(GetUserProfileResponse.builder()
                .requestId(request.getRequestId())
                .requestDateTime(request.getRequestDateTime())
                .channel(request.getChannel())
                .result(apiResultFactory.buildSuccess())
                .data(GetUserProfileResponse.GetUserProfileResponseData
                        .builder()
                        .userId(result.userId())
                        .username(result.username())
                        .email(result.email())
                        .phone(result.phone())
                        .fullName(result.fullName())
                        .status(result.status())
                        .emailVerified(result.emailVerified())
                        .phoneVerified(result.phoneVerified())
                        .createdAt(result.createdAt())
                        .updatedAt(result.updatedAt())
                        .build())
                .build());
    }

    @PostMapping(USER_SERVICE + UPDATE_PATH)
    public ResponseEntity<UpdateUserResponse> updateUser(@Valid @RequestBody UpdateUserRequest request) {
        UpdateUserResult result = userManagementService.updateUser(UpdateUserCommand.builder()
                .context(HttpUtils.toContext(request))
                .userId(request.getData().getUserId())
                .email(request.getData().getEmail())
                .phoneNumber(request.getData().getPhoneNumber())
                .avatarUrl(request.getData().getAvatarUrl())
                .address(request.getData().getAddress())
                .dob(request.getData().getDob())
                .gender(request.getData().getGender())
                .nationalId(request.getData().getNationalId())
                .phoneVerified(request.getData().getPhoneVerified())
                .profileCompleted(request.getData().getProfileCompleted())
                .emailVerified(request.getData().getEmailVerified())
                .status(request.getData().getStatus())
                .language(request.getData().getLanguage())
                .timezone(request.getData().getTimezone())
                .failLoginCount(request.getData().getFailLoginCount())
                .lastLoginAt(request.getData().getLastLoginAt())
                .lockedUntil(request.getData().getLockedUntil())
                .updatedBy(request.getData().getUpdatedBy())
                .build());

        UpdateUserResponse response = UpdateUserResponse.builder()
                .result(apiResultFactory.buildSuccess())
                .data(UpdateUserResponse.UpdateUserResponseData.builder()
                        .id(result.id())
                        .email(result.email())
                        .phoneNumber(result.phoneNumber())
                        .avatarUrl(result.avatarUrl())
                        .address(result.address())
                        .dob(result.dob())
                        .gender(result.gender())
                        .nationalId(result.nationalId())
                        .phoneVerified(result.phoneVerified())
                        .profileCompleted(result.profileCompleted())
                        .emailVerified(result.emailVerified())
                        .status(result.status())
                        .language(result.language())
                        .timezone(result.timezone())
                        .failLoginCount(result.failLoginCount())
                        .lastLoginAt(result.lastLoginAt())
                        .lockedUntil(result.lockedUntil())
                        .updatedAt(result.updatedAt())
                        .updatedBy(result.updatedBy())
                        .build())
                .build();

        return HttpUtils.buildResponse(request, response);
    }

    @PostMapping(USER_SERVICE + DELETE_PATH)
    public ResponseEntity<DeleteUserResponse> deleteUser(@Valid @RequestBody DeleteUserRequest request) {
        DeleteUserResult result = userManagementService.deleteUser(DeleteUserCommand.builder()
                .context(HttpUtils.toContext(request))
                .userId(request.getData().getUserId())
                .updatedBy(request.getData().getUpdatedBy())
                .build());

        DeleteUserResponse response = DeleteUserResponse.builder()
                .result(apiResultFactory.buildSuccess())
                .data(DeleteUserResponse.DeleteUserResponseData.builder()
                        .id(result.id())
                        .status(result.status())
                        .build())
                .build();

        return HttpUtils.buildResponse(request, response);
    }
}
