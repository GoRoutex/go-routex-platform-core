package platform.booking.service.domain.provinces.port;


import platform.booking.service.domain.provinces.readmodel.ProvincesFetchView;
import platform.booking.service.domain.provinces.readmodel.ProvincesSearchItem;
import platform.core.common.service.application.command.common.PagedResult;

import java.util.List;

public interface ProvincesQueryPort {
    List<ProvincesSearchItem> search(String merchantId, String keyword, int page, int size);


    PagedResult<ProvincesFetchView> fetchRoutes(String merchantId, int pageNumber, int pageSize);
}
