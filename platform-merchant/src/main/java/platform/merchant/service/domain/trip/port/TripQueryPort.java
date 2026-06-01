package platform.merchant.service.domain.trip.port;


import platform.core.common.service.application.command.common.PagedResult;
import platform.merchant.service.domain.trip.readmodel.TripFetchView;
import platform.core.common.service.application.readmodel.TripSearchView;

import java.util.List;

public interface TripQueryPort {
    List<TripSearchView> searchAssignedTrips(
            String merchantId,
            String origin,
            String destination,
            int pageNumber,
            int pageSize
    );

    PagedResult<TripFetchView> fetchTrips(String merchantId, List<String> merchantIds, int pageNumber, int pageSize);

}
