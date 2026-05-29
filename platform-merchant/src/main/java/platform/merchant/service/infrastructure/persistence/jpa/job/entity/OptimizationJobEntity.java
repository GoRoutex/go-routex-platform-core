package platform.merchant.service.infrastructure.persistence.jpa.job.entity;

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
import platform.merchant.service.domain.job.OptimizationJobStatus;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@SuperBuilder
@Table(name = "OPTIMIZATION_JOB")
public class OptimizationJobEntity extends AbstractAuditingEntity {

    @Id
    private String id;

    @Column(name = "MERCHANT_ID", nullable = false)
    private String merchantId;

    @Column(name = "ROUTE_ID", nullable = false)
    private String routeId;

    @Column(name = "STATUS", nullable = false)
    @Enumerated(EnumType.STRING)
    private OptimizationJobStatus status;

    @Column(name = "RECOMMENDATIONS_PAYLOAD", columnDefinition = "TEXT")
    private String recommendationsPayload;

    @Column(name = "CREATOR_EMAIL")
    private String creatorEmail;
}
