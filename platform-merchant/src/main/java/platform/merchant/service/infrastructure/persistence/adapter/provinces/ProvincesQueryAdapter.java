package platform.merchant.service.infrastructure.persistence.adapter.provinces;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import vn.com.go.routex.identity.security.log.SystemLog;
import platform.core.common.service.application.command.common.PagedResult;
import platform.merchant.service.domain.provinces.port.ProvincesQueryPort;
import platform.merchant.service.domain.provinces.readmodel.ProvincesFetchView;
import platform.merchant.service.domain.provinces.readmodel.ProvincesSearchItem;
import platform.merchant.service.infrastructure.persistence.jpa.provinces.entity.ProvincesEntity;
import platform.merchant.service.infrastructure.persistence.jpa.provinces.repository.ProvincesEntityRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ProvincesQueryAdapter implements ProvincesQueryPort {

    private final ProvincesEntityRepository provincesEntityRepository;
    private final SystemLog sLog = SystemLog.getLogger(this.getClass());

    @Override
    public List<ProvincesSearchItem> search(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(
                Math.max(0, page),
                Math.min(Math.max(size, 1), 50),
                Sort.by(Sort.Order.asc("name"))
        );

        return provincesEntityRepository.searchByKeyword(keyword == null ? "" : keyword.trim(), pageable)
                .map(p -> ProvincesSearchItem.builder()
                        .id(p.getId())
                        .name(p.getName())
                        .code(p.getCode())
                        .build())
                .getContent();
    }

    @Override
    public PagedResult<ProvincesFetchView> fetchProvinces(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Order.asc("name")));
        Page<ProvincesEntity> page = provincesEntityRepository.findAll(pageable);

        List<ProvincesFetchView> items = page.getContent().stream()
                .map(p -> {
                    return ProvincesFetchView.builder()
                            .id(p.getId())
                            .name(p.getName())
                            .code(p.getCode())
                            .build();
                })
                .toList();

        return PagedResult.<ProvincesFetchView>builder()
                .items(items)
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
    }
}
