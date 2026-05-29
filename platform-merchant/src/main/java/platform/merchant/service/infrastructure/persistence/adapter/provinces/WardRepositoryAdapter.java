package platform.merchant.service.infrastructure.persistence.adapter.provinces;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import platform.core.common.service.application.command.common.PagedResult;
import platform.merchant.service.domain.provinces.model.Ward;
import platform.merchant.service.domain.provinces.port.WardRepositoryPort;
import platform.merchant.service.infrastructure.persistence.jpa.provinces.entity.WardsEntity;
import platform.merchant.service.infrastructure.persistence.jpa.provinces.repository.WardsEntityRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class WardRepositoryAdapter implements WardRepositoryPort {

    private final WardsEntityRepository wardsEntityRepository;
    private final AdministrativePersistenceMapper administrativePersistenceMapper;

    @Override
    public Optional<Ward> findById(String id) {
        return wardsEntityRepository.findById(id).map(administrativePersistenceMapper::toDomain);
    }

    @Override
    public List<Ward> findByProvinceId(String provinceId) {
        return wardsEntityRepository.findByProvinceId(provinceId).stream()
                .map(administrativePersistenceMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public PagedResult<Ward> fetch(String provinceId, int pageNumber, int pageSize) {
        Page<WardsEntity> page = wardsEntityRepository.fetch(provinceId, PageRequest.of(pageNumber, pageSize));
        return PagedResult.<Ward>builder()
                .items(page.getContent().stream().map(administrativePersistenceMapper::toDomain).collect(Collectors.toList()))
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
    }

    @Override
    public PagedResult<Ward> search(String keyword, String provinceId, int page, int size) {
        Page<WardsEntity> resultPage = wardsEntityRepository.search(keyword, provinceId, PageRequest.of(page, size));
        return PagedResult.<Ward>builder()
                .items(resultPage.getContent().stream().map(administrativePersistenceMapper::toDomain).collect(Collectors.toList()))
                .pageNumber(resultPage.getNumber())
                .pageSize(resultPage.getSize())
                .totalElements(resultPage.getTotalElements())
                .totalPages(resultPage.getTotalPages())
                .build();
    }
}
