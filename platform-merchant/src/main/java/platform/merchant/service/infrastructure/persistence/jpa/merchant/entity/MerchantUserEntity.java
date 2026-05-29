package platform.merchant.service.infrastructure.persistence.jpa.merchant.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import platform.merchant.service.domain.merchant.MerchantUserStatus;
import platform.core.common.service.persistence.AbstractAuditingEntity;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Table(
        name = "MERCHANT_USERS",
        indexes = {
                @Index(name = "idx_merchant_users_merchant_id", columnList = "MERCHANT_ID")
        }
)
@Entity
public class MerchantUserEntity extends AbstractAuditingEntity {

    @Id
    private String id;

    @Column(name = "MERCHANT_ID", nullable = false)
    private String merchantId;

    @Column(name = "USER_ID", nullable = false, unique = true)
    private String userId;

    @Column(name = "ROLE_CODE", nullable = false)
    private String roleCode;

    @Column(name = "STATUS", nullable = false)
    @Enumerated(EnumType.STRING)
    private MerchantUserStatus status;

}
