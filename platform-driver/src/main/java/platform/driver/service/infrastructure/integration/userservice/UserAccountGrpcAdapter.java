package platform.driver.service.infrastructure.integration.userservice;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;
import platform.core.common.service.FetchUserAccountByIdRequest;
import platform.core.common.service.FetchUserAccountResponse;
import platform.core.common.service.UserAccountInfo;
import platform.core.common.service.UserAdminGrpcServiceGrpc;
import platform.core.common.service.UserAdminRequestContext;
import platform.driver.service.domain.user.model.User;
import platform.driver.service.domain.user.model.UserStatus;
import platform.driver.service.domain.user.port.UserRepositoryPort;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.UUID;

@Component
public class UserAccountGrpcAdapter implements UserRepositoryPort {

    @GrpcClient("userService")
    private UserAdminGrpcServiceGrpc.UserAdminGrpcServiceBlockingStub userAdminGrpcServiceStub;

    @Override
    public Optional<User> findById(String id) {
        try {
            FetchUserAccountResponse response = userAdminGrpcServiceStub.fetchUserAccountById(
                    FetchUserAccountByIdRequest.newBuilder()
                            .setUserId(id != null ? id : "")
                            .setContext(buildRequestContext())
                            .build()
            );
            return toUser(response.getUser());
        } catch (StatusRuntimeException ex) {
            if (ex.getStatus().getCode() == Status.Code.NOT_FOUND) {
                return Optional.empty();
            }
            throw ex;
        }
    }

    private UserAdminRequestContext buildRequestContext() {
        return UserAdminRequestContext.newBuilder()
                .setRequestId(UUID.randomUUID().toString())
                .setRequestDateTime(OffsetDateTime.now(ZoneOffset.ofHours(7)).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME))
                .setChannel("ONL")
                .build();
    }

    private Optional<User> toUser(UserAccountInfo info) {
        if (info == null || info.getId().isBlank()) {
            return Optional.empty();
        }
        return Optional.of(User.builder()
                .id(info.getId())
                .email(info.getEmail().isBlank() ? null : info.getEmail())
                .phoneNumber(info.getPhoneNumber().isBlank() ? null : info.getPhoneNumber())
                .status(info.getStatus().isBlank() ? null : UserStatus.valueOf(info.getStatus()))
                .build());
    }
}
