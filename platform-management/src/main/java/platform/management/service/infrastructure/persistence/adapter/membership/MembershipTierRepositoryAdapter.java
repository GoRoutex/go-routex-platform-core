package platform.management.service.infrastructure.persistence.adapter.membership;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import platform.management.service.domain.membership.model.MembershipTier;
import platform.management.service.domain.membership.port.MembershipTierRepositoryPort;
import platform.management.service.infrastructure.persistence.jpa.membership.repository.MembershipTierEntityRepository;

import java.util.Optional;


@Component
@RequiredArgsConstructor
public class MembershipTierRepositoryAdapter implements MembershipTierRepositoryPort {

    private final MembershipPersistenceMapper membershipPersistenceMapper;
    private final MembershipTierEntityRepository membershipTierEntityRepository;

    @Override
    public Optional<MembershipTier> findById(String id) {
        return membershipTierEntityRepository.findById(id)
                .map(membershipPersistenceMapper::toDomain);
    }

    @Override
    public Optional<MembershipTier> findByPriorityLevel(int i) {
        return membershipTierEntityRepository.findByPriorityLevel(i)
                .map(membershipPersistenceMapper::toDomain);

    }
}
