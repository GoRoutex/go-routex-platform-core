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
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Entity
@Table(name = "HOLIDAYS")
public class HolidayEntity extends AbstractAuditingEntity {

    @Id
    private String id;

    @Column(name = "HOLIDAY_DATE", nullable = false, unique = true)
    private LocalDate holidayDate;

    @Column(name = "NAME", nullable = false)
    private String name;

    @Column(name = "IS_PEAK_DAY", nullable = false)
    private Boolean isPeakDay;

    @Column(name = "SURCHARGE_RATE", precision = 5, scale = 2)
    private BigDecimal surchargeRate;

    @Column(name = "DESCRIPTION")
    private String description;
}

