package platform.management.service.application.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.com.go.routex.identity.security.log.SystemLog;
import platform.management.service.application.command.common.RequestContext;
import platform.management.service.application.command.user.GetUserProfileCommand;
import platform.management.service.application.command.user.GetUserProfileResult;
import platform.management.service.application.services.UserProfileService;
import platform.management.service.domain.user.model.User;
import platform.management.service.domain.user.port.UserRepositoryPort;
import platform.management.service.infrastructure.integration.common.support.InternalApiExecutor;
import platform.management.service.infrastructure.integration.userservice.client.UserServiceInternalClient;
import platform.management.service.infrastructure.integration.userservice.model.UserServiceFetchCustomersRequest;
import platform.management.service.infrastructure.integration.userservice.model.UserServiceInternalModels;
import platform.management.service.infrastructure.persistence.exception.BusinessException;
import platform.management.service.infrastructure.persistence.utils.ExceptionUtils;

import java.util.ArrayList;
import java.util.List;

import static platform.management.service.infrastructure.persistence.constant.ErrorConstant.RECORD_NOT_FOUND;
import static platform.management.service.infrastructure.persistence.constant.ErrorConstant.USER_NOT_FOUND_MESSAGE;

@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {

    private final UserRepositoryPort userRepositoryPort;
    private final UserAuthorizationService userAuthorizationService;
    private final UserServiceInternalClient userServiceInternalClient;

    private final SystemLog sLog = SystemLog.getLogger(this.getClass());

    @Override
    @Transactional(readOnly = true)
    public GetUserProfileResult getUserProfile(GetUserProfileCommand command) {
        RequestContext context = command.context();
        String userId = command.userId();
        User user = userRepositoryPort.findById(userId)
                .orElseThrow(() -> new BusinessException(context.requestId(), context.requestDateTime(), context.channel(),
                        ExceptionUtils.buildResultResponse(RECORD_NOT_FOUND, USER_NOT_FOUND_MESSAGE)));

        UserServiceFetchCustomersRequest request = new UserServiceFetchCustomersRequest();
        request.setUserIds(List.of(user.getId()));
        UserServiceInternalModels.CustomerData customer = InternalApiExecutor.execute(
                context,
                () -> userServiceInternalClient.fetchCustomersByUserIds(request)
        ).getItems().stream().findFirst().orElse(null);

        List<String> authorities = new ArrayList<>(userAuthorizationService.getAuthorities(user.getId()));

        GetUserProfileResult.CustomerProfileResult customerProfile = customer == null ? null
                : GetUserProfileResult.CustomerProfileResult.builder()
                .customerId(customer.getId())
                .tripPoints(customer.getTripPoints())
                .totalTrips(customer.getTotalTrips())
                .totalSpent(customer.getTotalSpent())
                .lastTripAt(customer.getLastTripAt())
                .build();

        return GetUserProfileResult.builder()
                .userId(userId)
                .email(user.getEmail())
                .phone(user.getPhoneNumber())
                .fullName(customer == null ? null : customer.getFullName())
                .status(user.getStatus())
                .emailVerified(user.getEmailVerified())
                .phoneVerified(user.getPhoneVerified())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .authorities(authorities)
                .customer(customerProfile)
                .build();
    }
}
