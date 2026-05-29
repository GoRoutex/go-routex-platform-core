package platform.merchant.service.application.command.provinces;

import lombok.Builder;

@Builder
public record SearchProvincesQuery(
        String keyword,
        int page,
        int size
) {
}

