package platform.merchant.service.application.command.wards;

import lombok.Builder;

import java.util.List;

@Builder
public record SearchWardsResult(
        List<SearchWardItem> data
) {
    @Builder
    public record SearchWardItem(
            String id,
            String name,
            String provinceId
    ) {}
}
