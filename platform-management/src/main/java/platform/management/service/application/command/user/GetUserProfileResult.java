package platform.management.service.application.command.user;

import lombok.Builder;
import platform.management.service.domain.user.model.UserStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Builder
public record GetUserProfileResult(
        String userId,
        String username,
        String email,
        String phone,
        String fullName,
        UserStatus status,
        Boolean emailVerified,
        Boolean phoneVerified,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt,
        List<String> authorities,
        CustomerProfileResult customer
) {
    @Builder
    public record CustomerProfileResult(
            String customerId,
            BigDecimal tripPoints,
            Integer totalTrips,
            BigDecimal totalSpent,
            OffsetDateTime lastTripAt
    ) {
    }
}

