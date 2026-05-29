package platform.merchant.service.application.query;

import platform.merchant.service.application.command.customer.CustomerMembershipView;

import java.util.Optional;

public interface CustomerMembershipQueryRepository {
    Optional<CustomerMembershipView> findMembershipSummaryByUserId(String userId);
}
