package platform.merchant.service.infrastructure.persistence.adapter.department;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import platform.core.common.service.application.command.common.PagedResult;
import platform.merchant.service.domain.department.DepartmentStatus;
import platform.merchant.service.domain.department.model.Department;
import platform.merchant.service.domain.department.port.DepartmentRepositoryPort;
import platform.merchant.service.infrastructure.persistence.jpa.department.entity.DepartmentEntity;
import platform.merchant.service.infrastructure.persistence.jpa.department.repository.DepartmentEntityRepository;

import java.util.List;
import java.util.Optional;


@Component
@RequiredArgsConstructor
public class DepartmentRepositoryAdapter implements DepartmentRepositoryPort {

    private final DepartmentEntityRepository departmentEntityRepository;
    private final DepartmentPersistenceMapper departmentPersistenceMapper;


    @Override
    public Optional<Department> findByName(String name, String merchantId) {
        return departmentEntityRepository.findByNameIgnoreCaseAndMerchantId(name, merchantId)
                .map(departmentPersistenceMapper::toDomain);
    }

    @Override
    public Optional<Department> findById(String id) {
        return departmentEntityRepository.findById(id).map(departmentPersistenceMapper::toDomain);
    }

    @Override
    public Optional<Department> findById(String id, String merchantId) {
        return departmentEntityRepository.findByIdAndMerchantId(id, merchantId)
                .map(departmentPersistenceMapper::toDomain);
    }

    @Override
    public boolean existsByName(String name) {
        return departmentEntityRepository.existsByNameIgnoreCase(name);
    }

    @Override
    public boolean existsByName(String name, String merchantId) {
        return departmentEntityRepository.existsByNameIgnoreCaseAndMerchantId(name, merchantId);
    }

    @Override
    public List<Department> findByMerchantId(String merchantId) {
        return departmentEntityRepository.findByMerchantId(merchantId).stream()
                .map(departmentPersistenceMapper::toDomain)
                .toList();
    }

    @Override
    public void save(Department department) {
        departmentEntityRepository.save(departmentPersistenceMapper.toEntity(department));
    }

    @Override
    public PagedResult<Department> fetch(int pageNumber, int pageSize) {
        Page<DepartmentEntity> page = departmentEntityRepository.findAll(PageRequest.of(pageNumber, pageSize));
        return toPagedResult(page);
    }

    @Override
    public PagedResult<Department> fetch(String merchantId, int pageNumber, int pageSize) {
        Page<DepartmentEntity> page = departmentEntityRepository.findByMerchantId(
                merchantId,
                PageRequest.of(pageNumber, pageSize)
        );
        return toPagedResult(page);
    }

    @Override
    public PagedResult<Department> fetch(String merchantId, String provinceId, int pageNumber, int pageSize) {
        Page<DepartmentEntity> page = departmentEntityRepository.findByMerchantIdAndProvinceId(merchantId, provinceId, PageRequest.of(pageNumber, pageSize));
        return toPagedResult(page);
    }

    @Override
    public PagedResult<Department> fetch(String merchantId, String provinceId, DepartmentStatus status, int pageNumber, int pageSize) {
        Page<DepartmentEntity> page = departmentEntityRepository.findByFilters(
                merchantId,
                provinceId,
                status,
                PageRequest.of(pageNumber, pageSize)
        );
        return toPagedResult(page);
    }

    @Override
    public List<Department> findAllByIdIn(List<String> departmentIds) {
        return departmentEntityRepository.findAllById(departmentIds)
                .stream().map(departmentPersistenceMapper::toDomain)
                .toList();
    }

    @Override
    public List<Department> search(String keyword, int page, int size) {
        return departmentEntityRepository.findByNameContainingIgnoreCase(keyword, PageRequest.of(page, size))
                .getContent()
                .stream()
                .map(departmentPersistenceMapper::toDomain)
                .toList();
    }

    private PagedResult<Department> toPagedResult(Page<DepartmentEntity> page) {
        List<Department> items = page.getContent().stream()
                .map(departmentPersistenceMapper::toDomain)
                .toList();

        return PagedResult.<Department>builder()
                .items(items)
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
    }
}
