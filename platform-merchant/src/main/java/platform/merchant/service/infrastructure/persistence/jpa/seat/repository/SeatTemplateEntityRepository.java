package platform.merchant.service.infrastructure.persistence.jpa.seat.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import platform.core.common.service.domain.seat.SeatFloor;
import platform.merchant.service.infrastructure.persistence.jpa.seat.entity.SeatTemplateEntity;

import java.util.List;

@Repository
public interface SeatTemplateEntityRepository extends JpaRepository<SeatTemplateEntity, String> {

    List<SeatTemplateEntity> findByVehicleTemplateId(String vehicleTemplateId);

    List<SeatTemplateEntity> findByVehicleTemplateIdAndFloor(String vehicleTemplateId, SeatFloor floor);
}
