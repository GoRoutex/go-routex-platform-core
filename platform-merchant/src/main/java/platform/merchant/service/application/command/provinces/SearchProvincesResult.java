package platform.merchant.service.application.command.provinces;

import lombok.Builder;

import java.util.List;

@Builder
public record SearchProvincesResult(
        List<SearchProvincesItemResult> data
) {
    @Builder
    public record SearchProvincesItemResult(
            String id,
            String name,
            String code,
            String type // PROVINCE, DEPARTMENT, STOP
    ) {
    }
}
