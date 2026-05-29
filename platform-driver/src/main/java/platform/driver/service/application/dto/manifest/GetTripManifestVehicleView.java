package platform.driver.service.application.dto.manifest;

import lombok.Builder;

@Builder
public record GetTripManifestVehicleView(
        String vehicleId,
        String plate,
        String vehicleType,
        Integer totalSeats
) {
}
