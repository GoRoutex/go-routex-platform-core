package platform.management.service.infrastructure.persistence.jpa.membership.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import platform.management.service.domain.membership.model.MembershipBadge;
import vn.com.routex.platform.common.persistence.AbstractAuditingEntity;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Entity(name = "Management_MembershipTierEntity")
@Table(name = "MEMBERSHIP_TIER")
public class MembershipTierEntity extends AbstractAuditingEntity {

    @Id
    private String id;

    @Column(name = "BADGE", nullable = false)
    @Enumerated(EnumType.STRING)
    private MembershipBadge badge;

    @Column(name = "MIN_POINTS")
    private BigDecimal minPoints;

    @Column(name = "POINT_MULTIPLIER")
    private BigDecimal pointMultiplier;

    @Column(name = "PRIORITY_LEVEL")
    private Integer priorityLevel;

    @Column(name = "DISCOUNT_PERCENT")
    private Integer discountPercent;
}
