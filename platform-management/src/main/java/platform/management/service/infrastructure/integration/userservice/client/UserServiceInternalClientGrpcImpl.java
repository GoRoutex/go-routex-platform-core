package platform.management.service.infrastructure.integration.userservice.client;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import platform.core.common.service.CustomerAdminInfo;
import platform.core.common.service.FetchCustomerByUserIdRequest;
import platform.core.common.service.FetchCustomerByUserIdResponse;
import platform.core.common.service.FetchCustomersByUserIdsRequest;
import platform.core.common.service.FetchCustomersByUserIdsResponse;
import platform.core.common.service.UserAdminGrpcServiceGrpc;
import platform.core.common.service.UserAdminRequestContext;
import platform.management.service.domain.customer.model.CustomerStatus;
import platform.management.service.infrastructure.integration.userservice.model.UserServiceFetchCustomersRequest;
import platform.management.service.infrastructure.integration.userservice.model.UserServiceInternalModels;
import platform.management.service.infrastructure.persistence.config.RequestAttributes;
import platform.management.service.infrastructure.persistence.constant.ErrorConstant;
import platform.management.service.infrastructure.persistence.exception.BusinessException;
import platform.management.service.infrastructure.persistence.utils.ExceptionUtils;
import platform.core.common.service.api.BaseResponse;
import platform.core.common.service.api.ApiResult;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
public class UserServiceInternalClientGrpcImpl implements UserServiceInternalClient {

    @GrpcClient("userService")
    private UserAdminGrpcServiceGrpc.UserAdminGrpcServiceBlockingStub userAdminGrpcServiceStub;

    @Override
    public BaseResponse<UserServiceInternalModels.CustomerData> fetchCustomerByUserId(String userId) {
        try {
            FetchCustomerByUserIdRequest request = FetchCustomerByUserIdRequest.newBuilder()
                    .setUserId(userId != null ? userId : "")
                    .setContext(buildRequestContext())
                    .build();

            FetchCustomerByUserIdResponse response = userAdminGrpcServiceStub.fetchCustomerByUserId(request);
            UserServiceInternalModels.CustomerData data = mapCustomerData(response.getCustomer());
            return successResponse(data);
        } catch (StatusRuntimeException ex) {
            handleGrpcException(ex);
            return null;
        }
    }

    @Override
    public BaseResponse<UserServiceInternalModels.CustomerListData> fetchCustomersByUserIds(UserServiceFetchCustomersRequest request) {
        try {
            FetchCustomersByUserIdsRequest grpcRequest = FetchCustomersByUserIdsRequest.newBuilder()
                    .addAllUserIds(request.getUserIds() != null ? request.getUserIds() : List.of())
                    .setContext(buildRequestContext())
                    .build();

            FetchCustomersByUserIdsResponse response = userAdminGrpcServiceStub.fetchCustomersByUserIds(grpcRequest);
            List<UserServiceInternalModels.CustomerData> items = response.getCustomersList().stream()
                    .map(this::mapCustomerData)
                    .toList();

            UserServiceInternalModels.CustomerListData data = new UserServiceInternalModels.CustomerListData(items);
            return successResponse(data);
        } catch (StatusRuntimeException ex) {
            handleGrpcException(ex);
            return null;
        }
    }

    private UserAdminRequestContext buildRequestContext() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        String requestId = null;
        String requestDateTime = null;
        String channel = null;
        if (attributes != null) {
            var request = attributes.getRequest();
            requestId = request.getHeader(RequestAttributes.REQUEST_ID);
            requestDateTime = request.getHeader(RequestAttributes.REQUEST_DATE_TIME);
            channel = request.getHeader(RequestAttributes.CHANNEL);
        }
        if (requestId == null || requestId.isBlank()) {
            requestId = UUID.randomUUID().toString();
        }
        if (requestDateTime == null || requestDateTime.isBlank()) {
            requestDateTime = OffsetDateTime.now(ZoneOffset.ofHours(7)).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        }
        if (channel == null || channel.isBlank()) {
            channel = "ONL";
        }
        return UserAdminRequestContext.newBuilder()
                .setRequestId(requestId)
                .setRequestDateTime(requestDateTime)
                .setChannel(channel)
                .build();
    }

    private <T> BaseResponse<T> successResponse(T data) {
        return BaseResponse.<T>builder()
                .result(ApiResult.builder()
                        .responseCode(ErrorConstant.SUCCESS_CODE)
                        .description("Success")
                        .build())
                .data(data) 
                .build();
    }

    private UserServiceInternalModels.CustomerData mapCustomerData(CustomerAdminInfo info) {
        if (info == null || info.getId().isEmpty()) {
            return null;
        }
        UserServiceInternalModels.CustomerData data = new UserServiceInternalModels.CustomerData();
        data.setId(info.getId());
        data.setUserId(info.getUserId());
        data.setFullName(info.getFullName());
        data.setStatus(info.getStatus().isEmpty() ? null : CustomerStatus.valueOf(info.getStatus()));
        data.setTotalTrips(info.getTotalTrips());
        data.setTripPoints(info.getTripPoints().isEmpty() ? BigDecimal.ZERO : new BigDecimal(info.getTripPoints()));
        data.setTotalSpent(info.getTotalSpent().isEmpty() ? BigDecimal.ZERO : new BigDecimal(info.getTotalSpent()));
        data.setLastBookingAt(info.getLastBookingAt().isEmpty() ? null : OffsetDateTime.parse(info.getLastBookingAt()));
        data.setLastTripAt(info.getLastTripAt().isEmpty() ? null : OffsetDateTime.parse(info.getLastTripAt()));
        return data;
    }

    private void handleGrpcException(StatusRuntimeException ex) {
        String errorMsg = ex.getStatus().getDescription();
        if (errorMsg == null) {
            errorMsg = ex.getMessage();
        }
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        String requestId = null;
        String requestDateTime = null;
        String channel = null;
        if (attributes != null) {
            var request = attributes.getRequest();
            requestId = request.getHeader(RequestAttributes.REQUEST_ID);
            requestDateTime = request.getHeader(RequestAttributes.REQUEST_DATE_TIME);
            channel = request.getHeader(RequestAttributes.CHANNEL);
        }
        if (requestId == null || requestId.isBlank()) {
            requestId = UUID.randomUUID().toString();
        }
        if (requestDateTime == null || requestDateTime.isBlank()) {
            requestDateTime = OffsetDateTime.now(ZoneOffset.ofHours(7)).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        }
        if (channel == null || channel.isBlank()) {
            channel = "ONL";
        }

        String responseCode = ErrorConstant.SYSTEM_ERROR;
        if (ex.getStatus().getCode() == Status.Code.NOT_FOUND || (errorMsg != null && errorMsg.contains("not found"))) {
            responseCode = ErrorConstant.RECORD_NOT_FOUND;
        }

        throw new BusinessException(
                requestId,
                requestDateTime,
                channel,
                ExceptionUtils.buildResultResponse(responseCode, errorMsg)
        );
    }
}
