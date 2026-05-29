package platform.driver.service.application.services;

import platform.driver.service.application.dto.manifest.GetTripManifestQuery;
import platform.driver.service.application.dto.manifest.TripManifestView;

public interface TripManifestService {
    TripManifestView getTripManifest(GetTripManifestQuery query);
}
