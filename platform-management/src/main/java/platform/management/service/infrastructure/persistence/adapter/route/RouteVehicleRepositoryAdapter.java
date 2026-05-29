package platform.management.service.infrastructure.persistence.adapter.route;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import vn.com.go.routex.identity.security.log.SystemLog;
import platform.management.service.domain.route.port.RouteVehicleRepositoryPort;
import platform.core.common.service.domain.vehicle.model.VehicleProfile;
import platform.merchant.service.infrastructure.persistence.jpa.vehicle.entity.VehicleEntity;
import platform.merchant.service.infrastructure.persistence.jpa.vehicle.entity.VehicleTemplateEntity;
import platform.merchant.service.infrastructure.persistence.jpa.vehicle.repository.VehicleEntityRepository;
import platform.merchant.service.infrastructure.persistence.jpa.vehicle.repository.VehicleTemplateRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RouteVehicleRepositoryAdapter implements RouteVehicleRepositoryPort {

    private final VehicleEntityRepository vehicleEntityRepository;
    private final RoutePersistenceMapper routePersistenceMapper;
    private final VehicleTemplateRepository vehicleTemplateRepository;
    private final SystemLog sLog = SystemLog.getLogger(this.getClass());

    @Override
    public Optional<VehicleProfile> findById(String vehicleId) {
        return vehicleEntityRepository.findById(vehicleId)
                .flatMap(this::toVehicleSnapshot);
    }

    @Override
    public Optional<VehicleProfile> findById(String vehicleId, String merchantId) {
        return vehicleEntityRepository.findByIdAndMerchantId(vehicleId, merchantId)
                .flatMap(this::toVehicleSnapshot);
    }

    @Override
    public Map<String, VehicleProfile> findByIds(List<String> vehicleIds) {
        List<VehicleEntity> listVehicle = vehicleEntityRepository.findByIdIn(vehicleIds);
        return toVehicleSnapshotMap(listVehicle);
    }

    @Override
    public Map<String, VehicleProfile> findByIds(List<String> vehicleIds, String merchantId) {
        return toVehicleSnapshotMap(vehicleEntityRepository.findByIdInAndMerchantId(vehicleIds, merchantId));
    }

    private Map<String, VehicleProfile> toVehicleSnapshotMap(List<VehicleEntity> vehicles) {
        Map<String, VehicleTemplateEntity> templatesById = vehicleTemplateRepository.findAllById(vehicles.stream()
                        .map(VehicleEntity::getTemplateId)
                        .distinct()
                        .toList())
                .stream()
                .collect(Collectors.toMap(VehicleTemplateEntity::getId, Function.identity()));

        return vehicles.stream()
                .map(vehicle -> toVehicleProfile(vehicle, templatesById.get(vehicle.getTemplateId())))
                .flatMap(Optional::stream)
                .collect(Collectors.toMap(VehicleProfile::getId, Function.identity()));
    }

    private Optional<VehicleProfile> toVehicleSnapshot(VehicleEntity vehicle) {
        return vehicleTemplateRepository.findById(vehicle.getTemplateId())
                .map(template -> routePersistenceMapper.toVehicleProfile(vehicle, template));
    }

    private Optional<VehicleProfile> toVehicleProfile(VehicleEntity vehicle, VehicleTemplateEntity template) {
        if (template == null) {
            return Optional.empty();
        }
        return Optional.of(routePersistenceMapper.toVehicleProfile(vehicle, template));
    }
}
