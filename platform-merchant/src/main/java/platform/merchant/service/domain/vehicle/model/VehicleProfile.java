package platform.core.common.service.domain.vehicle.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import platform.core.common.service.persistence.AbstractAuditingEntity;
import platform.core.common.service.domain.vehicle.VehicleStatus;

import java.time.OffsetDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public class VehicleProfile extends AbstractAuditingEntity {
    private String id;
    private String merchantId;
    private String templateId;
    private String creator;
    private boolean hasFloor;
    private VehicleStatus status;
    private String vehiclePlate;

    public static VehicleProfile register(
            String id,
            String merchantId,
            String templateId,
            String creator,
            boolean hasFloor,
            String vehiclePlate,
            OffsetDateTime createdAt
    ) {
        return VehicleProfile.builder()
                .id(id)
                .merchantId(merchantId)
                .templateId(templateId)
                .creator(creator)
                .hasFloor(hasFloor)
                .status(VehicleStatus.AVAILABLE)
                .vehiclePlate(vehiclePlate)
                .createdAt(createdAt)
                .createdBy(creator)
                .build();
    }
}
