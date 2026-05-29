package platform.core.common.service.domain.seat.port;

import platform.core.common.service.domain.seat.SeatFloor;
import platform.core.common.service.domain.seat.model.SeatTemplate;

import java.util.List;

public interface SeatTemplateRepositoryPort {

    List<SeatTemplate> findByVehicleTemplateId(String vehicleTemplateId);

    List<SeatTemplate> findByVehicleTemplateIdAndFloor(String vehicleTemplateId, SeatFloor floor);
}
