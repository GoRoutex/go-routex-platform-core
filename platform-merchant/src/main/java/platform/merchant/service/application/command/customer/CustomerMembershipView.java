package platform.merchant.service.application.command.customer;

import java.math.BigDecimal;

public record CustomerMembershipView(
        String userId,
        BigDecimal tripPoints,
        Integer totalTrips,
        BigDecimal totalSpent,
        String currentTierId,
        String currentBadge,
        Integer priorityLevel,
        BigDecimal pointMultiplier,
        Integer discountPercent
) {
}