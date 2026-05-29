package platform.merchant.service.domain.provinces.port;


import platform.core.common.service.application.command.common.PagedResult;
import platform.merchant.service.domain.provinces.model.Ward;

import java.util.List;
import java.util.Optional;

public interface WardRepositoryPort {
    Optional<Ward> findById(String id);
    List<Ward> findByProvinceId(String provinceId);
    PagedResult<Ward> fetch(String provinceId, int pageNumber, int pageSize);
    PagedResult<Ward> search(String keyword, String provinceId, int page, int size);
}
