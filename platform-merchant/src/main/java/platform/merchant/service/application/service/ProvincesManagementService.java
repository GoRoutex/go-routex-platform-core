package platform.merchant.service.application.service;


import platform.merchant.service.application.command.provinces.FetchProvincesQuery;
import platform.merchant.service.application.command.provinces.FetchProvincesResult;
import platform.merchant.service.application.command.provinces.SearchProvincesQuery;
import platform.merchant.service.application.command.provinces.SearchProvincesResult;

public interface ProvincesManagementService {
    SearchProvincesResult searchProvinces(SearchProvincesQuery query);

    FetchProvincesResult fetchProvinces(FetchProvincesQuery query);
}
