package platform.merchant.service.domain.authorities.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import platform.core.common.service.persistence.AbstractAuditingEntity;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public class RoleAggregate extends AbstractAuditingEntity {
    private String id;
    private String code;
    private String name;
    private String description;
    private Boolean enabled;
}
