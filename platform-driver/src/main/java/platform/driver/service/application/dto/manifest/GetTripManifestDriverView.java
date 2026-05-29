package platform.driver.service.application.dto.manifest;

import lombok.Builder;

@Builder
public record GetTripManifestDriverView(
        String driverId,
        String fullName,
        String phoneNumber
) {
}
