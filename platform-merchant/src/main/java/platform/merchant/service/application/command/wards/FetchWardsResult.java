package platform.merchant.service.application.command.wards;

import lombok.Builder;

import java.util.List;

@Builder
public record FetchWardsResult(
        List<WardItem> items,
        int pageNumber,
        int pageSize,
        long totalElements,
        int totalPages
) {
    @Builder
    public record WardItem(
            String id,
            String name,
            String provinceId
    ) {}
}
