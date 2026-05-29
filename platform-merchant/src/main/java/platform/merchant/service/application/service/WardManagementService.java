package platform.merchant.service.application.service;

import platform.merchant.service.application.command.wards.FetchWardsQuery;
import platform.merchant.service.application.command.wards.FetchWardsResult;
import platform.merchant.service.application.command.wards.SearchWardsQuery;
import platform.merchant.service.application.command.wards.SearchWardsResult;

public interface WardManagementService {
    FetchWardsResult fetchWards(FetchWardsQuery query);
    SearchWardsResult searchWards(SearchWardsQuery query);
}
