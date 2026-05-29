package platform.driver.service.application.dto.manifest;

import lombok.Builder;
import platform.core.common.service.common.RequestContext;

import java.util.List;

@Builder
public record TripManifestView(
        RequestContext requestContext,
        List<GetTripManifestBookingView> bookingInfo,
        GetTripManifestDriverView driverInfo,
        GetTripManifestVehicleView vehicleInfo,
        GetTripManifestSummaryView summary
) {
}

