package platform.management.service.infrastructure.integration.userservice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import platform.management.service.domain.customer.model.CustomerStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

public final class UserServiceInternalModels {

    private UserServiceInternalModels() {
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CustomerData {
        private String id;
        private String userId;
        private String fullName;
        private CustomerStatus status;
        private Integer totalTrips;
        private BigDecimal tripPoints;
        private BigDecimal totalSpent;
        private OffsetDateTime lastBookingAt;
        private OffsetDateTime lastTripAt;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CustomerListData {
        private List<CustomerData> items;
    }
}
