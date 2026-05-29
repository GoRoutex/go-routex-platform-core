package platform.merchant.service.domain.finance.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import platform.core.common.service.persistence.AbstractAuditingEntity;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class MerchantDailyStats extends AbstractAuditingEntity {
    private String id; // merchantId_date
    private String merchantId;
    private LocalDate statsDate;
    private Integer totalTickets;
    private BigDecimal totalRevenue;
    private BigDecimal totalDiscount;
    private BigDecimal merchantShare;
    private BigDecimal systemCommission;
    private Double occupancyRate;
}
