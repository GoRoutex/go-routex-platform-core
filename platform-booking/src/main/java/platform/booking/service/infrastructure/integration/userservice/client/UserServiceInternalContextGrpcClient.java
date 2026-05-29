package platform.booking.service.infrastructure.integration.userservice.client;

import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import platform.booking.service.infrastructure.integration.userservice.dto.FetchCustomerByUserIdClientResponse;
import platform.booking.service.infrastructure.integration.userservice.dto.FetchCustomerByUserIdClientResponseData;
import platform.core.common.service.FetchCustomerByUserIdRequest;
import platform.core.common.service.FetchCustomerByUserIdResponse;
import platform.core.common.service.UserAdminGrpcServiceGrpc;
import platform.core.common.service.UserAdminRequestContext;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
public class UserServiceInternalContextGrpcClient implements UserServiceInternalContextClient {

    @GrpcClient("userService")
    private UserAdminGrpcServiceGrpc.UserAdminGrpcServiceBlockingStub userAdminGrpcServiceStub;

    @Override
    public FetchCustomerByUserIdClientResponse fetchCustomerByUserId(String userId) {
        FetchCustomerByUserIdRequest request = FetchCustomerByUserIdRequest.newBuilder()
                .setUserId(userId != null ? userId : "")
                .setContext(buildRequestContext())
                .build();

        FetchCustomerByUserIdResponse grpcResponse = userAdminGrpcServiceStub.fetchCustomerByUserId(request);
        FetchCustomerByUserIdClientResponse response = new FetchCustomerByUserIdClientResponse();
        if (grpcResponse.hasCustomer()) {
            var customer = grpcResponse.getCustomer();
            response.setData(FetchCustomerByUserIdClientResponseData.builder()
                    .id(customer.getId())
                    .userId(customer.getUserId())
                    .fullName(customer.getFullName())
                    .build());
        }
        return response;
    }

    private UserAdminRequestContext buildRequestContext() {
        return UserAdminRequestContext.newBuilder()
                .setRequestId(UUID.randomUUID().toString())
                .setRequestDateTime(OffsetDateTime.now(ZoneOffset.ofHours(7)).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME))
                .setChannel("ONL")
                .build();
    }
}
