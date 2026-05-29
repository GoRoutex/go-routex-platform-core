package platform.merchant.service.infrastructure.persistence.jpa.finance.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
@Entity
@Table(name = "REVENUE_TRANSACTIONS")
public class RevenueTransactionEntity extends AbstractAuditingEntity {

    @Id
    private String id;

    @Column(name = "TICKET_ID", nullable = false)
    private String ticketId;

    @Column(name = "MERCHANT_ID", nullable = false)
    private String merchantId;

    @Column(name = "TOTAL_AMOUNT", nullable = false)
    private BigDecimal totalAmount;

    @Column(name = "DISCOUNT_AMOUNT")
    private BigDecimal discountAmount;

    @Column(name = "CAMPAIGN_ID")
    private String campaignId;

    @Column(name = "COMMISSION_RATE", nullable = false)
    private BigDecimal commissionRate;

    @Column(name = "SYSTEM_AMOUNT", nullable = false)
    private BigDecimal systemAmount;

    @Column(name = "MERCHANT_AMOUNT", nullable = false)
    private BigDecimal merchantAmount;

    @Column(name = "TRANSACTION_DATE", nullable = false)
    private OffsetDateTime transactionDate;
}
