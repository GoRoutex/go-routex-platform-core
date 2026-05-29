package platform.management.service.domain.membership.port;

import platform.management.service.domain.membership.model.MembershipTier;

import java.util.Optional;

public interface MembershipTierRepositoryPort {
    Optional<MembershipTier> findById(String id);

    Optional<MembershipTier> findByPriorityLevel(int i);
}
