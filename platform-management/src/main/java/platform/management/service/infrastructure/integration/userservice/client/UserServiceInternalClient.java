package platform.management.service.infrastructure.integration.userservice.client;

import platform.management.service.infrastructure.integration.userservice.model.UserServiceFetchCustomersRequest;
import platform.management.service.infrastructure.integration.userservice.model.UserServiceInternalModels;
import platform.core.common.service.api.BaseResponse;

public interface UserServiceInternalClient {

    BaseResponse<UserServiceInternalModels.CustomerData> fetchCustomerByUserId(String userId);

    BaseResponse<UserServiceInternalModels.CustomerListData> fetchCustomersByUserIds(
            UserServiceFetchCustomersRequest request
    );
}
