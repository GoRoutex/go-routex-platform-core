package platform.merchant.service.domain.merchant.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import platform.core.common.service.persistence.AbstractAuditingEntity;
import platform.merchant.service.domain.merchant.MerchantUserStatus;

import java.time.OffsetDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class MerchantUser extends AbstractAuditingEntity {

    private String id;
    private String merchantId;
    private String userId;
    private String roleCode;
    private MerchantUserStatus status;

    public static MerchantUser assign(
            String id,
            String merchantId,
            String userId,
            String roleCode,
            String actor,
            OffsetDateTime createdAt
    ) {
        return MerchantUser.builder()
                .id(id)
                .merchantId(merchantId)
                .userId(userId)
                .roleCode(roleCode)
                .status(MerchantUserStatus.ACTIVE)
                .createdBy(actor)
                .createdAt(createdAt)
                .build();
    }
}
