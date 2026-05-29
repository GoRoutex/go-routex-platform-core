package platform.merchant.service.infrastructure.persistence.jpa.authorities.entity;

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

@Entity(name = "Merchant_UserRolesEntity")
@Table(name = "USER_ROLES")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRolesEntity {

    @EmbeddedId
    private UserRoleIdEntity id;

    @Column(name = "ASSIGNED_AT")
    private OffsetDateTime assignedAt;
}
