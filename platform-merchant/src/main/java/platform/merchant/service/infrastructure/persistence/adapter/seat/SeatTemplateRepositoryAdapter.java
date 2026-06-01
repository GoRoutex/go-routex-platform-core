package platform.merchant.service.infrastructure.persistence.adapter.seat;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import platform.core.common.service.domain.seat.SeatFloor;
import platform.core.common.service.domain.seat.model.SeatTemplate;
import platform.merchant.service.domain.seat.port.SeatTemplateRepositoryPort;
import platform.merchant.service.infrastructure.persistence.jpa.seat.repository.SeatTemplateEntityRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class SeatTemplateRepositoryAdapter implements SeatTemplateRepositoryPort {

    private final SeatTemplateEntityRepository seatTemplateEntityRepository;
    private final SeatTemplatePersistenceMapper seatTemplatePersistenceMapper;

    @Override
    public List<SeatTemplate> findByVehicleTemplateId(String vehicleTemplateId) {
        return seatTemplateEntityRepository.findByVehicleTemplateId(vehicleTemplateId).stream()
                .map(seatTemplatePersistenceMapper::toDomain)
                .filter(SeatTemplate::isActive)
                .toList();
    }

    @Override
    public List<SeatTemplate> findByVehicleTemplateIdAndFloor(String vehicleTemplateId, SeatFloor floor) {
        return seatTemplateEntityRepository.findByVehicleTemplateIdAndFloor(vehicleTemplateId, floor).stream()
                .map(seatTemplatePersistenceMapper::toDomain)
                .filter(SeatTemplate::isActive)
                .toList();
    }


    @Override
    public Optional<SeatTemplate> findById(String id) {
        return seatTemplateEntityRepository.findById(id)
                .map(seatTemplatePersistenceMapper::toDomain);
    }

    @Override
    public List<SeatTemplate> findAllByIdIn(Set<String> seatTemplateIds) {
        return seatTemplateEntityRepository.findAllByIdIn(seatTemplateIds)
                .stream()
                .map(seatTemplatePersistenceMapper::toDomain)
                .toList();
    }
}
