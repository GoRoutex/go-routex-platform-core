package platform.merchant.service.infrastructure.persistence.jpa.maintenance.entity;


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
import platform.merchant.service.domain.maintenance.MaintenancePlanStatus;
import platform.merchant.service.domain.maintenance.MaintenancePlanType;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Entity
@Table(name = "MAINTENANCE_PLAN")
public class MaintenancePlanEntity extends AbstractAuditingEntity {

    @Id
    private String id;

    @Column(name = "MERCHANT_ID", nullable = false)
    private String merchantId;

    @Column(name = "VEHICLE_ID", nullable = false)
    private String vehicleId;

    @Column(name = "CODE", nullable = false)
    private String code;

    @Column(name = "TITLE", nullable = false)
    private String title;

    @Column(name = "DESCRIPTION")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "TYPE", nullable = false)
    private MaintenancePlanType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false)
    private MaintenancePlanStatus status;

    @Column(name = "PLANNED_DATE")
    private LocalDate plannedDate;

    @Column(name = "DUE_DATE")
    private LocalDate dueDate;

    @Column(name = "COMPLETED_DATE")
    private LocalDate completedDate;

    @Column(name = "CURRENT_ODOMETER_KM")
    private Long currentOdometerKm;

    @Column(name = "TARGET_ODOMETER_KM")
    private Long targetOdometerKm;

    @Column(name = "ESTIMATED_COST")
    private BigDecimal estimatedCost;

    @Column(name = "ACTUAL_COST")
    private BigDecimal actualCost;

    @Column(name = "SERVICE_PROVIDER")
    private String serviceProvider;

    @Column(name = "NOTE")
    private String note;
}
