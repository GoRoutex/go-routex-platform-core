package platform.booking.service.domain.customer.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import platform.core.common.service.persistence.AbstractAuditingEntity;

import java.math.BigDecimal;
import java.time.OffsetDateTime;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class CustomerMembership extends AbstractAuditingEntity {
    private String id;
    private String customerId;
    private String membershipTierId;
    private BigDecimal currentAvailablePoints; // Available points for gift exchanging
    private BigDecimal totalPoints; // Total points for promotion evaluating.
    private OffsetDateTime promotedAt;
    private CustomerMembershipStatus status;
}
