package platform.merchant.service.application.command.provinces;

import lombok.Builder;

import java.util.List;

@Builder
public record FetchProvincesResult(
        List<FetchProvinceResult> items,
        int pageNumber,
        int pageSize,
        long totalElements,
        int totalPages
) {

    @Builder
    public record FetchProvinceResult(
            String id,
            String code,
            String name
    ) {
    }
}
