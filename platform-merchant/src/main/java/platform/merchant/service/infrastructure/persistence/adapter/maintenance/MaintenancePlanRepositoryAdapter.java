package platform.merchant.service.infrastructure.persistence.adapter.maintenance;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import platform.core.common.service.application.command.common.PagedResult;
import platform.merchant.service.domain.maintenance.MaintenancePlanStatus;
import platform.merchant.service.domain.maintenance.MaintenancePlanType;
import platform.merchant.service.domain.maintenance.model.MaintenancePlan;
import platform.merchant.service.domain.maintenance.port.MaintenancePlanRepositoryPort;
import platform.merchant.service.infrastructure.persistence.jpa.maintenance.entity.MaintenancePlanEntity;
import platform.merchant.service.infrastructure.persistence.jpa.maintenance.repository.MaintenancePlanEntityRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MaintenancePlanRepositoryAdapter implements MaintenancePlanRepositoryPort {

    private final MaintenancePlanPersistenceMapper maintenancePlanPersistenceMapper;
    private final MaintenancePlanEntityRepository maintenancePlanEntityRepository;

    @Override
    public Optional<MaintenancePlan> findById(String id) {
        return maintenancePlanEntityRepository.findById(id)
                .map(maintenancePlanPersistenceMapper::toDomain);
    }

    @Override
    public Optional<MaintenancePlan> findById(String id, String merchantId) {
        return maintenancePlanEntityRepository.findByIdAndMerchantId(id, merchantId)
                .map(maintenancePlanPersistenceMapper::toDomain);
    }

    @Override
    public List<MaintenancePlan> findByVehicleId(String vehicleId, String merchantId) {
        return maintenancePlanEntityRepository.findByVehicleIdAndMerchantId(vehicleId, merchantId).stream()
                .map(maintenancePlanPersistenceMapper::toDomain)
                .toList();
    }

    @Override
    public boolean existsByCode(String code, String merchantId) {
        return maintenancePlanEntityRepository.existsByCodeAndMerchantId(code, merchantId);
    }

    @Override
    public void save(MaintenancePlan maintenancePlan) {
        maintenancePlanEntityRepository.save(maintenancePlanPersistenceMapper.toEntity(maintenancePlan));
    }

    @Override
    public PagedResult<MaintenancePlan> fetch(
            String merchantId,
            String vehicleId,
            MaintenancePlanStatus status,
            MaintenancePlanType type,
            LocalDate fromPlannedDate,
            LocalDate toPlannedDate,
            int pageNumber,
            int pageSize
    ) {
        Specification<MaintenancePlanEntity> specification = byMerchantId(merchantId)
                .and(hasVehicleId(vehicleId))
                .and(hasStatus(status))
                .and(hasType(type))
                .and(hasPlannedDateFrom(fromPlannedDate))
                .and(hasPlannedDateTo(toPlannedDate));

        Page<MaintenancePlanEntity> page = maintenancePlanEntityRepository.findAll(specification, PageRequest.of(pageNumber, pageSize));
        List<MaintenancePlan> items = page.getContent().stream()
                .map(maintenancePlanPersistenceMapper::toDomain)
                .toList();

        return PagedResult.<MaintenancePlan>builder()
                .items(items)
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
    }

    private Specification<MaintenancePlanEntity> byMerchantId(String merchantId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("merchantId"), merchantId);
    }

    private Specification<MaintenancePlanEntity> hasVehicleId(String vehicleId) {
        return (root, query, criteriaBuilder) ->
                vehicleId == null || vehicleId.isBlank()
                        ? criteriaBuilder.conjunction()
                        : criteriaBuilder.equal(root.get("vehicleId"), vehicleId);
    }

    private Specification<MaintenancePlanEntity> hasStatus(MaintenancePlanStatus status) {
        return (root, query, criteriaBuilder) ->
                status == null
                        ? criteriaBuilder.conjunction()
                        : criteriaBuilder.equal(root.get("status"), status);
    }

    private Specification<MaintenancePlanEntity> hasType(MaintenancePlanType type) {
        return (root, query, criteriaBuilder) ->
                type == null
                        ? criteriaBuilder.conjunction()
                        : criteriaBuilder.equal(root.get("type"), type);
    }

    private Specification<MaintenancePlanEntity> hasPlannedDateFrom(LocalDate fromPlannedDate) {
        return (root, query, criteriaBuilder) ->
                fromPlannedDate == null
                        ? criteriaBuilder.conjunction()
                        : criteriaBuilder.greaterThanOrEqualTo(root.get("plannedDate"), fromPlannedDate);
    }

    private Specification<MaintenancePlanEntity> hasPlannedDateTo(LocalDate toPlannedDate) {
        return (root, query, criteriaBuilder) ->
                toPlannedDate == null
                        ? criteriaBuilder.conjunction()
                        : criteriaBuilder.lessThanOrEqualTo(root.get("plannedDate"), toPlannedDate);
    }
}
