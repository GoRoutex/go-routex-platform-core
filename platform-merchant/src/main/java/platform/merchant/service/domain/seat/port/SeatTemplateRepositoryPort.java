package platform.merchant.service.domain.seat.port;

import platform.core.common.service.domain.seat.SeatFloor;
import platform.core.common.service.domain.seat.model.SeatTemplate;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface SeatTemplateRepositoryPort {

    List<SeatTemplate> findByVehicleTemplateId(String vehicleTemplateId);

    List<SeatTemplate> findByVehicleTemplateIdAndFloor(String vehicleTemplateId, SeatFloor floor);

    Optional<SeatTemplate> findById(String id);

    List<SeatTemplate> findAllByIdIn(Set<String> seatTemplateIds);
}
