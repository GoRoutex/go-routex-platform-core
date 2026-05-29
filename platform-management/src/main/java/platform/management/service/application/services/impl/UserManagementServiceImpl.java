package platform.management.service.application.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import platform.management.service.application.command.user.DeleteUserCommand;
import platform.management.service.application.command.user.DeleteUserResult;
import platform.management.service.application.command.user.FetchUserDetailQuery;
import platform.management.service.application.command.user.FetchUserDetailResult;
import platform.management.service.application.command.user.FetchUsersQuery;
import platform.management.service.application.command.user.FetchUsersResult;
import platform.management.service.application.command.user.UpdateUserCommand;
import platform.management.service.application.command.user.UpdateUserResult;
import platform.management.service.application.services.UserManagementService;
import platform.management.service.domain.common.PagedResult;
import platform.management.service.domain.user.model.User;
import platform.management.service.domain.user.model.UserStatus;
import platform.management.service.domain.user.port.UserRepositoryPort;
import platform.management.service.infrastructure.integration.common.support.InternalApiExecutor;
import platform.management.service.infrastructure.integration.userservice.client.UserServiceInternalClient;
import platform.management.service.infrastructure.integration.userservice.model.UserServiceFetchCustomersRequest;
import platform.management.service.infrastructure.integration.userservice.model.UserServiceInternalModels;
import platform.management.service.infrastructure.persistence.exception.BusinessException;
import platform.management.service.infrastructure.persistence.utils.ApiRequestUtils;
import platform.management.service.infrastructure.persistence.utils.ExceptionUtils;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static platform.management.service.infrastructure.persistence.constant.ErrorConstant.CUSTOMER_NOT_FOUND;
import static platform.management.service.infrastructure.persistence.constant.ErrorConstant.DUPLICATE_ERROR;
import static platform.management.service.infrastructure.persistence.constant.ErrorConstant.DUPLICATE_USER_EMAIL_MESSAGE;
import static platform.management.service.infrastructure.persistence.constant.ErrorConstant.DUPLICATE_USER_PHONE_MESSAGE;
import static platform.management.service.infrastructure.persistence.constant.ErrorConstant.INVALID_INPUT_ERROR;
import static platform.management.service.infrastructure.persistence.constant.ErrorConstant.INVALID_PAGE_NUMBER;
import static platform.management.service.infrastructure.persistence.constant.ErrorConstant.INVALID_PAGE_SIZE;
import static platform.management.service.infrastructure.persistence.constant.ErrorConstant.RECORD_NOT_FOUND;
import static platform.management.service.infrastructure.persistence.constant.ErrorConstant.USER_NOT_FOUND_BY_ID;

@Service
@RequiredArgsConstructor
public class UserManagementServiceImpl implements UserManagementService {

    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final int DEFAULT_PAGE_NUMBER = 1;

    private final UserRepositoryPort userRepositoryPort;
    private final UserServiceInternalClient userServiceInternalClient;

    @Override
    public FetchUsersResult fetchUsers(FetchUsersQuery query) {
        int pageSize = ApiRequestUtils.parseIntOrDefault(query.pageSize(), DEFAULT_PAGE_SIZE, "pageSize",
                query.context().requestId(), query.context().requestDateTime(), query.context().channel());
        int pageNumber = ApiRequestUtils.parseIntOrDefault(query.pageNumber(), DEFAULT_PAGE_NUMBER, "pageNumber",
                query.context().requestId(), query.context().requestDateTime(), query.context().channel());

        if (pageSize < 1 || pageSize > 100) {
            throw new BusinessException(query.context().requestId(), query.context().requestDateTime(), query.context().channel(),
                    ExceptionUtils.buildResultResponse(INVALID_INPUT_ERROR, INVALID_PAGE_SIZE));
        }
        if (pageNumber < 1) {
            throw new BusinessException(query.context().requestId(), query.context().requestDateTime(), query.context().channel(),
                    ExceptionUtils.buildResultResponse(INVALID_INPUT_ERROR, INVALID_PAGE_NUMBER));
        }

        PagedResult<User> page = userRepositoryPort.fetch(pageNumber - 1, pageSize);
        Map<String, UserServiceInternalModels.CustomerData> customersByUserId = fetchCustomersByUserIds(
                page.getItems().stream().map(User::getId).toList(),
                query
        );

        List<FetchUsersResult.FetchUserItemResult> items = page.getItems().stream()
                .map(item -> {
                    UserServiceInternalModels.CustomerData customer = customersByUserId.get(item.getId());
                    if (customer == null) {
                        throw new BusinessException(query.context().requestId(), query.context().requestDateTime(), query.context().channel(),
                                ExceptionUtils.buildResultResponse(RECORD_NOT_FOUND, CUSTOMER_NOT_FOUND));
                    }
                    return toFetchUserItem(item, customer);
                })
                .toList();

        return FetchUsersResult.builder()
                .items(items)
                .pageNumber(page.getPageNumber() + 1)
                .pageSize(page.getPageSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
    }

    @Override
    public FetchUserDetailResult fetchUserDetail(FetchUserDetailQuery query) {
        User user = loadActiveUser(query.userId(), query.context().requestId(), query.context().requestDateTime(),
                query.context().channel());
        return toFetchUserDetailResult(user);
    }

    @Override
    public UpdateUserResult updateUser(UpdateUserCommand command) {
        User existing = loadActiveUser(command.userId(), command.context().requestId(), command.context().requestDateTime(),
                command.context().channel());

        validateDuplicates(command, existing);

        User saved = userRepositoryPort.save(existing.toBuilder()
                .email(ApiRequestUtils.firstNonBlank(command.email(), existing.getEmail()))
                .phoneNumber(ApiRequestUtils.firstNonBlank(command.phoneNumber(), existing.getPhoneNumber()))
                .avatarUrl(ApiRequestUtils.firstNonBlank(command.avatarUrl(), existing.getAvatarUrl()))
                .address(ApiRequestUtils.firstNonBlank(command.address(), existing.getAddress()))
                .dob(command.dob() == null ? existing.getDob() : command.dob())
                .gender(command.gender() == null ? existing.getGender() : command.gender())
                .nationalId(ApiRequestUtils.firstNonBlank(command.nationalId(), existing.getNationalId()))
                .phoneVerified(command.phoneVerified() == null ? existing.getPhoneVerified() : command.phoneVerified())
                .profileCompleted(command.profileCompleted() == null ? existing.getProfileCompleted() : command.profileCompleted())
                .emailVerified(command.emailVerified() == null ? existing.getEmailVerified() : command.emailVerified())
                .status(command.status() == null ? existing.getStatus() : command.status())
                .language(ApiRequestUtils.firstNonBlank(command.language(), existing.getLanguage()))
                .timezone(ApiRequestUtils.firstNonBlank(command.timezone(), existing.getTimezone()))
                .failLoginCount(command.failLoginCount() == null ? existing.getFailLoginCount() : command.failLoginCount())
                .lastLoginAt(command.lastLoginAt() == null ? existing.getLastLoginAt() : command.lastLoginAt())
                .lockedUntil(command.lockedUntil() == null ? existing.getLockedUntil() : command.lockedUntil())
                .updatedBy(ApiRequestUtils.firstNonBlank(command.updatedBy(), existing.getUpdatedBy()))
                .build());

        return toUpdateUserResult(saved);
    }

    @Override
    public DeleteUserResult deleteUser(DeleteUserCommand command) {
        User existing = loadActiveUser(command.userId(), command.context().requestId(), command.context().requestDateTime(),
                command.context().channel());

        User saved = userRepositoryPort.save(existing.toBuilder()
                .status(UserStatus.DELETED)
                .updatedBy(ApiRequestUtils.firstNonBlank(command.updatedBy(), existing.getUpdatedBy()))
                .build());

        return DeleteUserResult.builder()
                .id(saved.getId())
                .status(saved.getStatus())
                .build();
    }

    private Map<String, UserServiceInternalModels.CustomerData> fetchCustomersByUserIds(List<String> userIds, FetchUsersQuery query) {
        UserServiceFetchCustomersRequest request = new UserServiceFetchCustomersRequest();
        request.setUserIds(userIds);

        return InternalApiExecutor.execute(
                query.context(),
                () -> userServiceInternalClient.fetchCustomersByUserIds(request)
        ).getItems().stream().collect(Collectors.toMap(
                UserServiceInternalModels.CustomerData::getUserId,
                Function.identity(),
                (left, right) -> left
        ));
    }

    private void validateDuplicates(UpdateUserCommand command, User existing) {
        if (command.email() != null && !command.email().isBlank()
                && userRepositoryPort.existsByEmailAndIdNot(command.email().trim(), existing.getId())) {
            throw new BusinessException(command.context().requestId(), command.context().requestDateTime(), command.context().channel(),
                    ExceptionUtils.buildResultResponse(DUPLICATE_ERROR,
                            String.format(DUPLICATE_USER_EMAIL_MESSAGE, command.email().trim())));
        }

        if (command.phoneNumber() != null && !command.phoneNumber().isBlank()
                && userRepositoryPort.existsByPhoneNumberAndIdNot(command.phoneNumber().trim(), existing.getId())) {
            throw new BusinessException(command.context().requestId(), command.context().requestDateTime(), command.context().channel(),
                    ExceptionUtils.buildResultResponse(DUPLICATE_ERROR,
                            String.format(DUPLICATE_USER_PHONE_MESSAGE, command.phoneNumber().trim())));
        }
    }

    private User loadActiveUser(String userId, String requestId, String requestDateTime, String channel) {
        User user = userRepositoryPort.findById(userId)
                .orElseThrow(() -> new BusinessException(
                        requestId,
                        requestDateTime,
                        channel,
                        ExceptionUtils.buildResultResponse(RECORD_NOT_FOUND, String.format(USER_NOT_FOUND_BY_ID, userId))
                ));

        if (user.getStatus() == UserStatus.DELETED) {
            throw new BusinessException(
                    requestId,
                    requestDateTime,
                    channel,
                    ExceptionUtils.buildResultResponse(RECORD_NOT_FOUND, String.format(USER_NOT_FOUND_BY_ID, userId))
            );
        }

        return user;
    }

    private FetchUsersResult.FetchUserItemResult toFetchUserItem(User user, UserServiceInternalModels.CustomerData customer) {
        return FetchUsersResult.FetchUserItemResult.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(customer.getFullName())
                .phoneNumber(user.getPhoneNumber())
                .avatarUrl(user.getAvatarUrl())
                .dob(user.getDob())
                .gender(user.getGender())
                .phoneVerified(user.getPhoneVerified())
                .profileCompleted(user.getProfileCompleted())
                .emailVerified(user.getEmailVerified())
                .status(user.getStatus())
                .language(user.getLanguage())
                .timezone(user.getTimezone())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    private FetchUserDetailResult toFetchUserDetailResult(User user) {
        return FetchUserDetailResult.builder()
                .id(user.getId())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .avatarUrl(user.getAvatarUrl())
                .address(user.getAddress())
                .dob(user.getDob())
                .gender(user.getGender())
                .nationalId(user.getNationalId())
                .phoneVerified(user.getPhoneVerified())
                .profileCompleted(user.getProfileCompleted())
                .emailVerified(user.getEmailVerified())
                .status(user.getStatus())
                .language(user.getLanguage())
                .timezone(user.getTimezone())
                .failLoginCount(user.getFailLoginCount())
                .lastLoginAt(user.getLastLoginAt())
                .lockedUntil(user.getLockedUntil())
                .createdAt(user.getCreatedAt())
                .createdBy(user.getCreatedBy())
                .updatedAt(user.getUpdatedAt())
                .updatedBy(user.getUpdatedBy())
                .build();
    }

    private UpdateUserResult toUpdateUserResult(User user) {
        return UpdateUserResult.builder()
                .id(user.getId())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .avatarUrl(user.getAvatarUrl())
                .address(user.getAddress())
                .dob(user.getDob())
                .gender(user.getGender())
                .nationalId(user.getNationalId())
                .phoneVerified(user.getPhoneVerified())
                .profileCompleted(user.getProfileCompleted())
                .emailVerified(user.getEmailVerified())
                .status(user.getStatus())
                .language(user.getLanguage())
                .timezone(user.getTimezone())
                .failLoginCount(user.getFailLoginCount())
                .lastLoginAt(user.getLastLoginAt())
                .lockedUntil(user.getLockedUntil())
                .updatedAt(user.getUpdatedAt())
                .updatedBy(user.getUpdatedBy())
                .build();
    }
}
