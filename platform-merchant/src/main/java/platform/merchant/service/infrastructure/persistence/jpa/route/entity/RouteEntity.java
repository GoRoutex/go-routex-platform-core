package platform.merchant.service.infrastructure.persistence.jpa.route.entity;


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
import platform.merchant.service.domain.route.RouteStatus;
import platform.core.common.service.persistence.AbstractAuditingEntity;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@SuperBuilder
@Table(name = "ROUTE")
public class RouteEntity extends AbstractAuditingEntity {

    @Id
    private String id;

    @Column(name = "ORIGIN_CODE")
    private String originCode;

    @Column(name = "DESTINATION_CODE")
    private String destinationCode;

    @Column(name = "ORIGIN_PROVINCE_ID")
    private String originProvinceId;

    @Column(name = "DESTINATION_PROVINCE_ID")
    private String destinationProvinceId;

    @Column(name = "ORIGIN_DEPARTMENT_ID")
    private String originDepartmentId;

    @Column(name = "ORIGIN_DEPARTMENT_NAME")
    private String originDepartmentName;

    @Column(name = "DESTINATION_DEPARTMENT_NAME")
    private String destinationDepartmentName;

    @Column(name = "DESTINATION_DEPARTMENT_ID")
    private String destinationDepartmentId;

    @Column(name = "ORIGIN_NAME")
    private String originName;

    @Column(name = "DESTINATION_NAME")
    private String destinationName;

    @Column(name = "DURATION")
    private Long duration;

    @Column(name = "DISTANCE")
    private Long distance;

    @Column(name = "CREATOR")
    private String creator;

    @Column(name = "MERCHANT_ID")
    private String merchantId;

    @Column(name = "STATUS")
    @Enumerated(EnumType.STRING)
    private RouteStatus status;
}
