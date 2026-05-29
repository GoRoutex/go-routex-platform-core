package platform.merchant.service.application.command.provinces;

import lombok.Builder;

@Builder
public record UpdateProvinceResult(
        int id,
        String name,
        String code
) {
}

