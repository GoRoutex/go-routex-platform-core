package platform.merchant.service.application.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import platform.core.common.service.application.command.common.PagedResult;
import platform.merchant.service.application.command.wards.FetchWardsQuery;
import platform.merchant.service.application.command.wards.FetchWardsResult;
import platform.merchant.service.application.command.wards.SearchWardsQuery;
import platform.merchant.service.application.command.wards.SearchWardsResult;
import platform.merchant.service.application.service.WardManagementService;
import platform.merchant.service.domain.provinces.model.Ward;
import platform.merchant.service.domain.provinces.port.WardRepositoryPort;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WardManagementServiceImpl implements WardManagementService {

    private final WardRepositoryPort wardRepositoryPort;

    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final int DEFAULT_PAGE_NUMBER = 1;

    @Override
    public FetchWardsResult fetchWards(FetchWardsQuery query) {
        int pageSize = parseIntOrDefault(query.pageSize(), DEFAULT_PAGE_SIZE);
        int pageNumber = parseIntOrDefault(query.pageNumber(), DEFAULT_PAGE_NUMBER);

        PagedResult<Ward> result = wardRepositoryPort.fetch(query.provinceId(), pageNumber - 1, pageSize);

        return FetchWardsResult.builder()
                .items(result.getItems().stream()
                        .map(w -> FetchWardsResult.WardItem.builder()
                                .id(w.getId())
                                .name(w.getName())
                                .provinceId(w.getProvinceId())
                                .build())
                        .collect(Collectors.toList()))
                .pageNumber(result.getPageNumber() + 1)
                .pageSize(result.getPageSize())
                .totalElements(result.getTotalElements())
                .totalPages(result.getTotalPages())
                .build();
    }

    @Override
    public SearchWardsResult searchWards(SearchWardsQuery query) {
        PagedResult<Ward> result = wardRepositoryPort.search(query.keyword(), query.provinceId(), query.page(), query.size());

        return SearchWardsResult.builder()
                .data(result.getItems().stream()
                        .map(w -> SearchWardsResult.SearchWardItem.builder()
                                .id(w.getId())
                                .name(w.getName())
                                .provinceId(w.getProvinceId())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }

    private int parseIntOrDefault(String v, int defaultValue) {
        if (v == null || v.isBlank()) return defaultValue;
        try {
            return Integer.parseInt(v);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}
