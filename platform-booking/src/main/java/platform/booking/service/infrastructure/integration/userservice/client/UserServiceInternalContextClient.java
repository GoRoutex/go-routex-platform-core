package platform.booking.service.infrastructure.integration.userservice.client;

import platform.booking.service.infrastructure.integration.userservice.dto.FetchCustomerByUserIdClientResponse;

public interface UserServiceInternalContextClient {

    FetchCustomerByUserIdClientResponse fetchCustomerByUserId(String userId);
}
