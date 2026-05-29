package platform.merchant.service.infrastructure.persistence.adapter.seat;

import org.springframework.stereotype.Component;
import platform.core.common.service.domain.seat.model.SeatTemplate;
import platform.merchant.service.infrastructure.persistence.jpa.seat.entity.SeatTemplateEntity;

@Component
public class SeatTemplatePersistenceMapper {

    public SeatTemplate toDomain(SeatTemplateEntity entity) {
        if (entity == null) {
            return null;
        }

        return SeatTemplate.builder()
                .id(entity.getId())
                .vehicleTemplateId(entity.getVehicleTemplateId())
                .seatCode(entity.getSeatCode())
                .floor(entity.getFloor())
                .rowNo(entity.getRowNo())
                .columnNo(entity.getColumnNo())
                .type(entity.getType())
                .isActive(entity.isActive())
                .createdAt(entity.getCreatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedAt(entity.getUpdatedAt())
                .updatedBy(entity.getUpdatedBy())
                .build();
    }

    public SeatTemplateEntity toEntity(SeatTemplate seatTemplate) {
        if(seatTemplate == null) {
            return null;
        }

        return SeatTemplateEntity.builder()
                .id(seatTemplate.getId())
                .vehicleTemplateId(seatTemplate.getVehicleTemplateId())
                .seatCode(seatTemplate.getSeatCode())
                .floor(seatTemplate.getFloor())
                .rowNo(seatTemplate.getRowNo())
                .columnNo(seatTemplate.getColumnNo())
                .type(seatTemplate.getType())
                .isActive(seatTemplate.isActive())
                .createdAt(seatTemplate.getCreatedAt())
                .createdBy(seatTemplate.getCreatedBy())
                .updatedAt(seatTemplate.getUpdatedAt())
                .updatedBy(seatTemplate.getUpdatedBy())
                .build();
    }
}
