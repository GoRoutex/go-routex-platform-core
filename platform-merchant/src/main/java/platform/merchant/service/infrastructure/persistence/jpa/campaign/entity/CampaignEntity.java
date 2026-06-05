package platform.merchant.service.infrastructure.persistence.jpa.campaign.entity;

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
import platform.core.common.service.persistence.AbstractAuditingEntity;
import platform.merchant.service.domain.campaign.CampaignStatus;
import platform.merchant.service.domain.campaign.DiscountType;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Entity
@Table(name = "CAMPAIGN")
public class CampaignEntity extends AbstractAuditingEntity {
    @Id
    @Column(name = "ID")
    private String id;

    @Column(name = "MERCHANT_ID")
    private String merchantId;

    @Column(name = "NAME")
    private String name;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "PROMOTION_CODE", unique = true)
    private String promotionCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "DISCOUNT_TYPE")
    private DiscountType discountType;

    @Column(name = "DISCOUNT_VALUE")
    private BigDecimal discountValue;

    @Column(name = "MAX_DISCOUNT_AMOUNT")
    private BigDecimal maxDiscountAmount;

    @Column(name = "MIN_ORDER_AMOUNT")
    private BigDecimal minOrderAmount;

    @Column(name = "START_DATE")
    private OffsetDateTime startDate;

    @Column(name = "END_DATE")
    private OffsetDateTime endDate;

    @Column(name = "USAGE_LIMIT")
    private Integer usageLimit;

    @Column(name = "USED_COUNT")
    private Integer usedCount;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS")
    private CampaignStatus status;
}
