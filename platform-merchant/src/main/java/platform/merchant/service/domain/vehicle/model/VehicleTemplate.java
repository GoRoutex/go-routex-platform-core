package platform.merchant.service.domain.vehicle.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import platform.core.common.service.domain.vehicle.FuelType;
import platform.core.common.service.domain.vehicle.VehicleTemplateCategory;
import platform.core.common.service.domain.vehicle.VehicleTemplateStatus;
import platform.core.common.service.persistence.AbstractAuditingEntity;
import platform.merchant.service.domain.vehicle.VehicleTemplateType;

import java.math.BigDecimal;


@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class VehicleTemplate extends AbstractAuditingEntity {
    private String id;
    private String code;
    private String name;
    private String manufacturer;
    private String model;
    private Long seatCapacity;
    private VehicleTemplateCategory category;
    private VehicleTemplateType type;
    private FuelType fuelType;
    private boolean hasFloor;
    private BigDecimal ticketPrice;
    private String merchantId;
    private VehicleTemplateStatus status;
}
