package platform.merchant.service.application.command.provinces;

import lombok.Builder;

@Builder
public record DeleteProvinceResult(
        int id
) {
}

