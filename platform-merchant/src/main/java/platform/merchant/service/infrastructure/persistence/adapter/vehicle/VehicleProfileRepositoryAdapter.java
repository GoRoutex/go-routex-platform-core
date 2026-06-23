package platform.merchant.service.infrastructure.persistence.adapter.vehicle;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import platform.core.common.service.application.command.common.PagedResult;
import platform.core.common.service.domain.vehicle.VehicleStatus;
import platform.merchant.service.domain.vehicle.model.VehicleProfile;
import platform.merchant.service.domain.vehicle.port.VehicleProfileRepositoryPort;
import platform.merchant.service.infrastructure.persistence.adapter.route.RoutePersistenceMapper;
import platform.merchant.service.infrastructure.persistence.jpa.vehicle.entity.VehicleEntity;
import platform.merchant.service.infrastructure.persistence.jpa.vehicle.entity.VehicleTemplateEntity;
import platform.merchant.service.infrastructure.persistence.jpa.vehicle.repository.VehicleEntityRepository;
import platform.merchant.service.infrastructure.persistence.jpa.vehicle.repository.VehicleTemplateRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class VehicleProfileRepositoryAdapter implements VehicleProfileRepositoryPort {

    private final VehicleEntityRepository vehicleEntityRepository;
    private final VehicleTemplateRepository vehicleTemplateRepository;
    private final VehiclePersistenceMapper vehiclePersistenceMapper;
    private final RoutePersistenceMapper routePersistenceMapper;


    @Override
    public Optional<VehicleProfile> findById(String vehicleId) {
        return vehicleEntityRepository.findById(vehicleId)
                .flatMap(this::toVehicleProfile);
    }

    @Override
    public Optional<VehicleProfile> findById(String vehicleId, String merchantId) {
        return vehicleEntityRepository.findByIdAndMerchantId(vehicleId, merchantId)
                .flatMap(this::toVehicleProfile);
    }

    @Override
    public Map<String, VehicleProfile> findByIds(List<String> vehicleIds) {
        return toVehicleProfileMap(vehicleEntityRepository.findByIdIn(vehicleIds));
    }

    @Override
    public Map<String, VehicleProfile> findByIds(List<String> vehicleIds, String merchantId) {
        return toVehicleProfileMap(vehicleEntityRepository.findByIdInAndMerchantId(vehicleIds, merchantId));
    }

    @Override
    public List<VehicleProfile> findByIdIn(Set<String> vehicleIds) {
        return vehicleEntityRepository.findByIdIn(vehicleIds)
                .stream().map(vehiclePersistenceMapper::toDomain)
                .collect(Collectors.toList());
    }

    private Map<String, VehicleProfile> toVehicleProfileMap(List<VehicleEntity> vehicles) {
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

    private Optional<VehicleProfile> toVehicleProfile(VehicleEntity vehicle) {
        return vehicleTemplateRepository.findById(vehicle.getTemplateId())
                .map(template -> routePersistenceMapper.toVehicleProfile(vehicle, template));
    }

    private Optional<VehicleProfile> toVehicleProfile(VehicleEntity vehicle, VehicleTemplateEntity template) {
        if (template == null) {
            return Optional.empty();
        }
        return Optional.of(routePersistenceMapper.toVehicleProfile(vehicle, template));
    }
    @Override
    public boolean existsByVehiclePlate(String vehiclePlate) {
        return vehicleEntityRepository.existsByVehiclePlate(vehiclePlate);
    }

    @Override
    public boolean existsByVehiclePlate(String vehiclePlate, String merchantId) {
        return vehicleEntityRepository.existsByVehiclePlateAndMerchantId(vehiclePlate, merchantId);
    }
    @Override
    public List<VehicleProfile> findByMerchantId(String merchantId) {
        return vehicleEntityRepository.findByMerchantId(merchantId).stream()
                .map(vehiclePersistenceMapper::toDomain)
                .toList();
    }

    @Override
    public List<VehicleProfile> findByTemplateId(String templateId, String merchantId) {
        return vehicleEntityRepository.findByTemplateIdAndMerchantId(templateId, merchantId).stream()
                .map(vehiclePersistenceMapper::toDomain)
                .toList();
    }

    @Override
    public void save(VehicleProfile vehicleProfile) {
        vehicleEntityRepository.save(vehiclePersistenceMapper.toEntity(vehicleProfile));
    }

    @Override
    public void saveAll(List<VehicleProfile> vehicleProfiles) {
        List<VehicleEntity> entities = vehicleProfiles.stream()
                .map(vehiclePersistenceMapper::toEntity)
                .toList();
        vehicleEntityRepository.saveAll(entities);
    }

    @Override
    public PagedResult<VehicleProfile> fetch(int pageNumber, int pageSize) {
        Page<VehicleEntity> page = vehicleEntityRepository.findAll(PageRequest.of(pageNumber, pageSize));
        return toPagedResult(page);
    }

    @Override
    public PagedResult<VehicleProfile> fetch(String merchantId, VehicleStatus status, int pageNumber, int pageSize) {
        Page<VehicleEntity> page = vehicleEntityRepository.findByMerchantIdAndStatus(
                merchantId,
                status.name(),
                PageRequest.of(pageNumber, pageSize)
        );
        return toPagedResult(page);
    }

    @Override
    public PagedResult<VehicleProfile> fetch(String merchantId, int pageNumber, int pageSize) {
        Page<VehicleEntity> page = vehicleEntityRepository.findByMerchantId(merchantId, PageRequest.of(pageNumber, pageSize));
        return toPagedResult(page);
    }

    private PagedResult<VehicleProfile> toPagedResult(Page<VehicleEntity> page) {
        List<VehicleProfile> items = page.getContent().stream()
                .map(vehiclePersistenceMapper::toDomain)
                .toList();

        return PagedResult.<VehicleProfile>builder()
                .items(items)
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
    }
}
