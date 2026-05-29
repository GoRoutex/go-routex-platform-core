package platform.management.service.infrastructure.integration.userservice.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserServiceFetchCustomersRequest {
    private List<String> userIds;
}
