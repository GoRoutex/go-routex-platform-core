package platform.management.service.infrastructure.persistence.adapter.membership;


import org.springframework.stereotype.Component;
import platform.management.service.domain.membership.model.MembershipTier;
import platform.management.service.infrastructure.persistence.jpa.membership.entity.MembershipTierEntity;

@Component
public class MembershipPersistenceMapper {

    public MembershipTierEntity toEntity(MembershipTier membershipTier) {
        return MembershipTierEntity.builder()
                .id(membershipTier.getId())
                .badge(membershipTier.getBadge())
                .minPoints(membershipTier.getMinPoints())
                .pointMultiplier(membershipTier.getPointMultiplier())
                .priorityLevel(membershipTier.getPriorityLevel())
                .discountPercent(membershipTier.getDiscountPercent())
                .createdAt(membershipTier.getCreatedAt())
                .updatedAt(membershipTier.getUpdatedAt())
                .createdBy(membershipTier.getCreatedBy())
                .updatedBy(membershipTier.getUpdatedBy())
                .build();
    }


    public MembershipTier toDomain(MembershipTierEntity membershipEntity) {
        return MembershipTier.builder()
                .id(membershipEntity.getId())
                .badge(membershipEntity.getBadge())
                .minPoints(membershipEntity.getMinPoints())
                .pointMultiplier(membershipEntity.getPointMultiplier())
                .priorityLevel(membershipEntity.getPriorityLevel())
                .discountPercent(membershipEntity.getDiscountPercent())
                .createdAt(membershipEntity.getCreatedAt())
                .updatedAt(membershipEntity.getUpdatedAt())
                .createdBy(membershipEntity.getCreatedBy())
                .updatedBy(membershipEntity.getUpdatedBy())
                .build();
    }


}
