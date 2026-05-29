package platform.merchant.service.infrastructure.persistence.adapter.driver;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import platform.core.common.service.application.command.common.PagedResult;
import platform.merchant.service.domain.driver.OperationStatus;
import platform.merchant.service.domain.driver.model.DriverProfile;
import platform.merchant.service.domain.driver.port.DriverProfileRepositoryPort;
import platform.merchant.service.infrastructure.persistence.jpa.driver.entity.DriverProfileEntity;
import platform.merchant.service.infrastructure.persistence.jpa.driver.repository.DriverProfileEntityRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DriverProfileRepositoryAdapter implements DriverProfileRepositoryPort {
    private final DriverProfileEntityRepository driverProfileEntityRepository;
    private final DriverProfilePersistenceMapper driverProfilePersistenceMapper;

    @Override
    public Optional<DriverProfile> findById(String id) {
        return driverProfileEntityRepository.findById(id).map(driverProfilePersistenceMapper::toDomain);
    }

    @Override
    public Optional<DriverProfile> findById(String id, String merchantId) {
        return driverProfileEntityRepository.findByIdAndMerchantId(id, merchantId)
                .map(driverProfilePersistenceMapper::toDomain);
    }

    @Override
    public Optional<DriverProfile> findByUserId(String userId) {
        return driverProfileEntityRepository.findByUserId(userId).map(driverProfilePersistenceMapper::toDomain);
    }

    @Override
    public Optional<DriverProfile> findByUserId(String userId, String merchantId) {
        return driverProfileEntityRepository.findByUserIdAndMerchantId(userId, merchantId)
                .map(driverProfilePersistenceMapper::toDomain);
    }

    @Override
    public Optional<DriverProfile> findByEmployeeCode(String employeeCode, String merchantId) {
        return driverProfileEntityRepository.findByEmployeeCodeAndMerchantId(employeeCode, merchantId)
                .map(driverProfilePersistenceMapper::toDomain);
    }

    @Override
    public boolean existsByUserId(String userId, String merchantId) {
        return driverProfileEntityRepository.existsByUserIdAndMerchantId(userId, merchantId);
    }

    @Override
    public boolean existsByEmployeeCode(String employeeCode, String merchantId) {
        return driverProfileEntityRepository.existsByEmployeeCodeAndMerchantId(employeeCode, merchantId);
    }

    @Override
    public List<DriverProfile> findByMerchantId(String merchantId) {
        return driverProfileEntityRepository.findByMerchantId(merchantId).stream()
                .map(driverProfilePersistenceMapper::toDomain)
                .toList();
    }

    @Override
    public PagedResult<DriverProfile> fetch(String merchantId, int pageNumber, int pageSize) {
        Page<DriverProfileEntity> page = driverProfileEntityRepository.findByMerchantId(merchantId, PageRequest.of(pageNumber, pageSize));
        return toPagedResult(page);
    }

    @Override
    public PagedResult<DriverProfile> fetch(String merchantId, OperationStatus status, int pageNumber, int pageSize) {
        Page<DriverProfileEntity> page = driverProfileEntityRepository.findByMerchantIdAndOperationStatus(
                merchantId,
                status,
                PageRequest.of(pageNumber, pageSize)
        );
        return toPagedResult(page);
    }

    private PagedResult<DriverProfile> toPagedResult(Page<DriverProfileEntity> page) {
        List<DriverProfile> items = page.getContent().stream()
                .map(driverProfilePersistenceMapper::toDomain)
                .toList();


        return PagedResult.<DriverProfile>builder()
                .items(items)
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
    }
    @Override
    public DriverProfile save(DriverProfile profile) {
        return driverProfilePersistenceMapper.toDomain(driverProfileEntityRepository.save(driverProfilePersistenceMapper.toEntity(profile)));
    }

    @Override
    public List<DriverProfile> findByIdIn(Set<String> vehicleIds) {
        return driverProfileEntityRepository.findByIdIn(vehicleIds)
                .stream()
                .map(driverProfilePersistenceMapper::toDomain)
                .toList();
    }
}
