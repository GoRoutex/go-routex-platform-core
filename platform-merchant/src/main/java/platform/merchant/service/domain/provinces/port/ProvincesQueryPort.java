package platform.merchant.service.domain.provinces.port;

import platform.core.common.service.application.command.common.PagedResult;
import platform.merchant.service.domain.provinces.readmodel.ProvincesFetchView;
import platform.merchant.service.domain.provinces.readmodel.ProvincesSearchItem;

import java.util.List;

public interface ProvincesQueryPort {
    List<ProvincesSearchItem> search(String keyword, int page, int size);

    PagedResult<ProvincesFetchView> fetchProvinces(int pageNumber, int pageSize);
}
