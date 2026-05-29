package platform.management.service.domain.role.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import platform.core.common.service.persistence.AbstractAuditingEntity;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Roles extends AbstractAuditingEntity {

    private String id;

    private String code;

    private String name;

    private String description;

    private Boolean enabled;

    private Set<Authorities> authorities = new HashSet<>();
}
