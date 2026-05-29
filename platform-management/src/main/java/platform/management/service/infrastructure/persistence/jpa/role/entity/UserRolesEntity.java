package platform.management.service.infrastructure.persistence.jpa.role.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@Entity(name = "Management_UserRolesEntity")
@Table(name = "USER_ROLES")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRolesEntity {

    @EmbeddedId
    private UserRoleEntityId id;

    @Column(name = "ASSIGNED_AT")
    private OffsetDateTime assignedAt;
}
