package platform.merchant.service.domain.route.model;

import lombok.Builder;

@Builder
public record ProvincesInformationPair(
        String originCode,
        String destinationCode,
        String originName,
        String destinationName) {
}
