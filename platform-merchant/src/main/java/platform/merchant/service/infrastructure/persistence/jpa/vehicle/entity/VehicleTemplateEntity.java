package platform.merchant.service.infrastructure.persistence.jpa.vehicle.entity;


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
import platform.core.common.service.domain.vehicle.FuelType;
import platform.core.common.service.domain.vehicle.VehicleTemplateCategory;
import platform.core.common.service.domain.vehicle.VehicleTemplateStatus;
import platform.core.common.service.domain.vehicle.VehicleTemplateType;
import platform.core.common.service.persistence.AbstractAuditingEntity;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Entity
@Table(name = "VEHICLE_TEMPLATE")
public class VehicleTemplateEntity extends AbstractAuditingEntity {

    @Id
    private String id;

    @Column(name = "CODE")
    private String code;

    @Column(name = "NAME")
    private String name;

    @Column(name = "MANUFACTURER")
    private String manufacturer;

    @Column(name = "MODEL")
    private String model;

    @Column(name = "SEAT_CAPACITY")
    private Long seatCapacity;

    @Column(name = "CATEGORY")
    @Enumerated(EnumType.STRING)
    private VehicleTemplateCategory category;

    @Column(name = "TYPE")
    @Enumerated(EnumType.STRING)
    private VehicleTemplateType type;

    @Column(name = "FUEL_TYPE")
    @Enumerated(EnumType.STRING)
    private FuelType fuelType;

    @Column(name = "HAS_FLOOR")
    private boolean hasFloor;

    @Column(name = "TICKET_PRICE")
    private BigDecimal ticketPrice;

    @Column(name = "MERCHANT_ID")
    private String merchantId;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS")
    private VehicleTemplateStatus status;

}
